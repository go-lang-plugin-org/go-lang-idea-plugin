package com.goide.psi.impl;

import com.goide.GoSdkUtil;
import com.goide.psi.*;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.resolve.ResolveCache;
import com.intellij.psi.scope.BaseScopeProcessor;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ArrayUtil;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.containers.OrderedSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public class GoReference extends PsiPolyVariantReferenceBase<GoReferenceExpression> {
  private static final Set<String> RESERVED_NAMES = ContainerUtil.newHashSet("print", "println");

  private static final ResolveCache.PolyVariantResolver<PsiPolyVariantReferenceBase> MY_RESOLVER =
    new ResolveCache.PolyVariantResolver<PsiPolyVariantReferenceBase>() {
      @NotNull
      @Override
      public ResolveResult[] resolve(@NotNull PsiPolyVariantReferenceBase psiPolyVariantReferenceBase, boolean incompleteCode) {
        return ((GoReference)psiPolyVariantReferenceBase).resolveInner();
      }
    };

  public GoReference(GoReferenceExpression element) {
    super(element, TextRange.from(element.getIdentifier().getStartOffsetInParent(), element.getIdentifier().getTextLength()));
  }

  @NotNull
  private ResolveResult[] resolveInner() {
    String identifierText = getName();
    Collection<ResolveResult> result = new OrderedSet<ResolveResult>();
    processResolveVariants(createResolveProcessor(identifierText, result));
    return result.toArray(new ResolveResult[result.size()]);
  }

  private String getName() {
    return myElement.getIdentifier().getText();
  }

  private MyScopeProcessor createResolveProcessor(final String text, final Collection<ResolveResult> result) {
    return new MyScopeProcessor() {
      @Override
      public boolean execute(@NotNull PsiElement element, ResolveState state) {
        if (element.equals(myElement)) return !result.add(new PsiElementResolveResult(element));
        if (element instanceof PsiNamedElement) {
          String name = ((PsiNamedElement)element).getName();
          if (text.equals(name)) {
            result.add(new PsiElementResolveResult(element));
            return false;
          }
        }
        return true;
      }
    };
  }

  abstract static class MyScopeProcessor extends BaseScopeProcessor {
    boolean isCompletion() {
      return false;
    }
  }

  private static MyScopeProcessor createCompletionProcessor(final Collection<LookupElement> variants) {
    return new MyScopeProcessor() {
      @Override
      public boolean execute(@NotNull PsiElement element, ResolveState state) {
        LookupElement lookup = createLookup(element);
        if (lookup != null) variants.add(lookup);
        return true;
      }

      @Nullable
      private LookupElement createLookup(PsiElement element) {
        if (element instanceof GoFunctionOrMethodDeclaration) {
          return GoPsiImplUtil.createFunctionOrMethodLookupElement((GoFunctionOrMethodDeclaration)element);
        }
        else if (element instanceof GoTypeSpec) {
          return GoPsiImplUtil.createTypeConversionLookupElement((GoTypeSpec)element);
        }
        else if (element instanceof GoNamedElement) {
          return GoPsiImplUtil.createVariableLikeLookupElement((GoNamedElement)element);
        }
        else if (element instanceof PsiDirectory) {
          return GoPsiImplUtil.createPackageLookupElement(((PsiDirectory)element).getName(), true);
        }
        else if (element instanceof PsiNamedElement) {
          return LookupElementBuilder.create((PsiNamedElement)element);
        }
        return null;
      }

      @Override
      boolean isCompletion() {
        return true;
      }
    };
  }

  @Override
  @NotNull
  public ResolveResult[] multiResolve(final boolean incompleteCode) {
    return ResolveCache.getInstance(myElement.getProject()).resolveWithCaching(this, MY_RESOLVER, false, false);
  }

  @NotNull
  @Override
  public Object[] getVariants() {
    List<LookupElement> variants = ContainerUtil.newArrayList();
    processResolveVariants(createCompletionProcessor(variants));
    return ArrayUtil.toObjectArray(variants);
  }

  private boolean processResolveVariants(@NotNull MyScopeProcessor processor) {
    PsiFile file = myElement.getContainingFile();
    if (!(file instanceof GoFile)) return false;
    ResolveState state = ResolveState.initial();
    GoReferenceExpression qualifier = myElement.getQualifier();
    if (qualifier != null) {
      return processQualifierExpression(((GoFile)file), qualifier, processor, state);
    }
    return processUnqualifiedResolve(((GoFile)file), processor, state, true);
  }

  private boolean processQualifierExpression(@NotNull GoFile file,
                                             @NotNull GoReferenceExpression qualifier,
                                             @NotNull MyScopeProcessor processor,
                                             @NotNull ResolveState state) {
    PsiReference reference = qualifier.getReference();
    PsiElement target = reference != null ? reference.resolve() : null;
    if (target == null || target == qualifier) return false;
    if (target instanceof PsiDirectory) {
      processDirectory((PsiDirectory)target, file, processor, state, false); // todo: local resolve or not?
    }
    else if (target instanceof GoNamedElement) {
      GoType type = ((GoNamedElement)target).getGoType();
      if (type != null) processGoType(type, processor, state);
    }
    return false;
  }

  private boolean processGoType(@NotNull GoType type, @NotNull MyScopeProcessor processor, @NotNull ResolveState state) {
    if (!processExistingType(type, processor, state)) return false;
    if (type instanceof GoPointerType) type = ((GoPointerType)type).getType();

    GoTypeReferenceExpression reference = GoPsiImplUtil.getTypeReference(type);
    return processInTypeRef(reference, type, processor, state);
  }

  private boolean processExistingType(@NotNull GoType type, @NotNull MyScopeProcessor processor, @NotNull ResolveState state) {
    if (type instanceof GoStructType) {
      GoScopeProcessorBase delegate = createDelegate(processor);
      type.processDeclarations(delegate, ResolveState.initial(), null, myElement);
      Collection<? extends GoNamedElement> result = delegate.getVariants();
      if (!processNamedElements(processor, state, true, result)) return false;

      final List<GoTypeReferenceExpression> refs = ContainerUtil.newArrayList();
      type.accept(new GoRecursiveVisitor() {
        @Override
        public void visitAnonymousFieldDefinition(@NotNull GoAnonymousFieldDefinition o) {
          refs.add(o.getTypeReferenceExpression());
        }
      });
      for (GoTypeReferenceExpression ref : refs) {
        if (!processInTypeRef(ref, type, processor, state)) return false;
      }
    }
    PsiElement parent = type.getParent();
    if (parent instanceof GoTypeSpec) {
      List<GoMethodDeclaration> methods = ((GoTypeSpec)parent).getMethods();
      if (!processNamedElements(processor, state, true, methods)) return false;
    }
    return true;
  }

  private boolean processInTypeRef(@Nullable GoTypeReferenceExpression refExpr,
                                   @Nullable GoType recursiveStopper,
                                   @NotNull MyScopeProcessor processor,
                                   @NotNull ResolveState state) {
    PsiReference reference = refExpr != null ? refExpr.getReference() : null;
    PsiElement resolve = reference != null ? reference.resolve() : null;
    if (resolve instanceof GoTypeSpec) {
      GoType resolveType = ((GoTypeSpec)resolve).getType();
      if (resolveType != null && (recursiveStopper == null || !resolveType.textMatches(recursiveStopper)) &&
          !processExistingType(resolveType, processor, state)) {
        return false;
      }
    }
    return true;
  }


  protected void processDirectory(@Nullable PsiDirectory dir,
                                  @Nullable GoFile file,
                                  @NotNull MyScopeProcessor processor,
                                  @NotNull ResolveState state,
                                  boolean localProcessing) {
    String name = file != null ? file.getName() : null;
    if (dir != null) {
      for (PsiFile psiFile : dir.getFiles()) {
        if (psiFile instanceof GoFile && !psiFile.getName().equals(name)) {
          processFileEntities((GoFile)psiFile, processor, state, localProcessing);
        }
      }
    }
  }

  private boolean processUnqualifiedResolve(@NotNull GoFile file,
                                            @NotNull MyScopeProcessor processor,
                                            @NotNull ResolveState state,
                                            boolean localResolve) {
    String id = getName();
    if ("_".equals(id)) return processSelf(processor, state);

    PsiElement parent = myElement.getParent();

    if (parent instanceof GoSelectorExpr) {
      return processSelector((GoSelectorExpr)parent, processor, state, myElement);
    }

    PsiElement grandPa = parent.getParent();
    if (grandPa instanceof GoSelectorExpr) {
      if (!processSelector((GoSelectorExpr)grandPa, processor, state, parent)) return false;
    }

    GoScopeProcessorBase delegate = createDelegate(processor);
    ResolveUtil.treeWalkUp(myElement, delegate);
    processReceiver(delegate);
    processFunctionParameters(delegate);
    Collection<? extends GoNamedElement> result = delegate.getVariants();
    if (!processNamedElements(processor, state, localResolve, result)) return false;

    processFileEntities(file, processor, state, localResolve);

    if (RESERVED_NAMES.contains(id)) return processSelf(processor, state);

    Collection<? extends PsiElement> collection = file.getImportMap().values();
    for (Object o : collection) {
      if (o instanceof GoImportSpec) processor.execute((PsiElement)o, state);
      if (o instanceof GoImportString) {
        PsiDirectory resolve = ((GoImportString)o).resolve();
        if (resolve != null) processor.execute(resolve, state);
      }
    }

    GoFile builtinFile = GoSdkUtil.findBuiltinFile(myElement);
    if (builtinFile != null) {
      processFileEntities(builtinFile, processor, state, true);
      for (GoNamedElement definition : builtinFile.getTypes()) {
        if (!definition.isPublic()) processor.execute(definition, state);
      }
    }
    return true;
  }

  private boolean processSelector(@NotNull GoSelectorExpr parent,
                                  @NotNull MyScopeProcessor processor,
                                  @NotNull ResolveState state,
                                  @Nullable PsiElement another) {
    List<GoExpression> list = parent.getExpressionList();
    if (list.size() > 1 && list.get(1).isEquivalentTo(another)) {
      GoType type = list.get(0).getGoType();
      if (type != null && !processGoType(type, processor, state)) return false;
    }
    return true;
  }

  private GoVarProcessor createDelegate(MyScopeProcessor processor) {
    return new GoVarProcessor(getName(), myElement, processor.isCompletion());
  }

  private static void processFileEntities(GoFile file, MyScopeProcessor processor, ResolveState state, boolean localResolve) {
    processNamedElements(processor, state, localResolve, file.getConsts());
    processNamedElements(processor, state, localResolve, file.getVars());
    processNamedElements(processor, state, localResolve, file.getFunctions());
  }

  private static boolean processNamedElements(PsiScopeProcessor processor,
                                              ResolveState state,
                                              boolean localResolve,
                                              Collection<? extends GoNamedElement> elements) {
    for (GoNamedElement definition : elements) {
      if ((definition.isPublic() || localResolve) && !processor.execute(definition, state)) return false;
    }
    return true;
  }

  private boolean processSelf(PsiScopeProcessor processor, ResolveState state) {
    return processor.execute(myElement, state);
  }

  //@Nullable
  //@Override
  //protected PsiElement processUnqualified(@NotNull GoFile file, boolean localResolve) {
  //  String id = myElement.getIdentifier().getText();
  //  if ("_".equals(id)) return myElement; // todo: need a better solution


  //
  //  return resolveImportOrPackage(file, id);
  //}

  private void processFunctionParameters(@NotNull GoScopeProcessorBase processor) {
    // todo: nested functions from FunctionLit
    GoFunctionOrMethodDeclaration function = PsiTreeUtil.getParentOfType(myElement, GoFunctionOrMethodDeclaration.class);
    GoSignature signature = function != null ? function.getSignature() : null;
    GoParameters parameters;
    if (signature != null) {
      parameters = signature.getParameters();
      parameters.processDeclarations(processor, ResolveState.initial(), null, myElement);
      GoResult result = signature.getResult();
      GoParameters resultParameters = result != null ? result.getParameters() : null;
      if (resultParameters != null) resultParameters.processDeclarations(processor, ResolveState.initial(), null, myElement);
    }
  }

  private void processReceiver(@NotNull GoScopeProcessorBase processor) {
    GoMethodDeclaration method = PsiTreeUtil.getParentOfType(myElement, GoMethodDeclaration.class); // todo: nested methods?
    GoReceiver receiver = method != null ? method.getReceiver() : null;
    if (receiver != null) receiver.processDeclarations(processor, ResolveState.initial(), null, myElement);
  }

  @NotNull
  @Override
  public PsiElement handleElementRename(@NotNull String newElementName) throws IncorrectOperationException {
    myElement.getIdentifier().replace(GoElementFactory.createIdentifierFromText(myElement.getProject(), newElementName));
    return myElement;
  }
}
