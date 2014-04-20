package com.goide.psi.impl;

import com.goide.GoSdkUtil;
import com.goide.psi.*;
import com.goide.util.GoUtil;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.util.Key;
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

import static com.goide.psi.impl.GoPsiImplUtil.*;

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
  public static final Key<List<PsiElement>> IMPORT_USERS = Key.create("IMPORT_USERS");

  public GoReference(@NotNull GoReferenceExpression element) {
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

  @NotNull
  private MyScopeProcessor createResolveProcessor(@NotNull final String text, @NotNull final Collection<ResolveResult> result) {
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

  @NotNull
  private static MyScopeProcessor createCompletionProcessor(@NotNull final Collection<LookupElement> variants) {
    return new MyScopeProcessor() {
      @Override
      public boolean execute(@NotNull PsiElement element, ResolveState state) {
        LookupElement lookup = createLookup(element);
        if (lookup != null) variants.add(lookup);
        return true;
      }

      @Nullable
      private LookupElement createLookup(@NotNull PsiElement element) {
        // @formatter:off
        if (element instanceof GoSignatureOwner)     return createFunctionOrMethodLookupElement((GoSignatureOwner)element);
        else if (element instanceof GoTypeSpec)      return createTypeConversionLookupElement((GoTypeSpec)element);
        else if (element instanceof GoNamedElement)  return createVariableLikeLookupElement((GoNamedElement)element);
        else if (element instanceof PsiDirectory)    return createPackageLookupElement(((PsiDirectory)element).getName(), true);
        else if (element instanceof PsiNamedElement) return LookupElementBuilder.create((PsiNamedElement)element);
        // @formatter:on
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
    PsiElement target = qualifier.getReference().resolve();
    if (target == null || target == qualifier) return false;
    if (target instanceof GoImportSpec) target = ((GoImportSpec)target).getImportString().resolve();
    if (target instanceof PsiDirectory) {
      processDirectory((PsiDirectory)target, file, null, processor, state, false);
    }
    else if (target instanceof GoTypeOwner) {
      GoType type = ((GoTypeOwner)target).getGoType();
      if (type != null) processGoType(type, processor, state);
    }
    return false;
  }

  private boolean processGoType(@NotNull GoType type, @NotNull MyScopeProcessor processor, @NotNull ResolveState state) {
    if (!processExistingType(type, processor, state)) return false;
    GoType returnType = type;
    if (type instanceof GoPointerType) returnType = ((GoPointerType)type).getType();

    GoTypeReferenceExpression reference = getTypeReference(returnType);
    return processInTypeRef(reference, returnType, processor, state);
  }

  private boolean processExistingType(@NotNull GoType type, @NotNull MyScopeProcessor processor, @NotNull ResolveState state) {
    PsiFile file = type.getContainingFile();
    if (!(file instanceof GoFile)) return true;
    PsiFile myFile = myElement.getContainingFile();
    if (!(myFile instanceof GoFile)) return true;
    boolean localResolve = Comparing.equal(((GoFile)myFile).getFullPackageName(), ((GoFile)file).getFullPackageName());

    if (type instanceof GoStructType) {
      GoScopeProcessorBase delegate = createDelegate(processor);
      type.processDeclarations(delegate, ResolveState.initial(), null, myElement);
      Collection<? extends GoNamedElement> result = delegate.getVariants();
      if (!processNamedElements(processor, state, result, localResolve)) return false;

      final List<GoTypeReferenceExpression> refs = ContainerUtil.newArrayList();
      type.accept(new GoRecursiveVisitor() {
        @Override
        public void visitAnonymousFieldDefinition(@NotNull GoAnonymousFieldDefinition o) {
          refs.add(o.getTypeReferenceExpression());
        }
      });
      if (!processCollectedRefs(type, refs, processor, state)) return false;
    }
    else if (type instanceof GoInterfaceType) {
      if (!processNamedElements(processor, state, ((GoInterfaceType)type).getMethods(), localResolve)) return false;
      if (!processCollectedRefs(type, ((GoInterfaceType)type).getBaseTypesReferences(), processor, state)) return false;
    }

    PsiElement parent = type.getParent();
    if (parent instanceof GoTypeSpec && !processNamedElements(processor, state, ((GoTypeSpec)parent).getMethods(), localResolve)) return false;
    return true;
  }

  private boolean processCollectedRefs(@NotNull GoType type,
                                       @NotNull List<GoTypeReferenceExpression> refs,
                                       @NotNull MyScopeProcessor processor,
                                       @NotNull ResolveState state) {
    for (GoTypeReferenceExpression ref : refs) {
      if (!processInTypeRef(ref, type, processor, state)) return false;
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

  protected boolean processDirectory(@Nullable PsiDirectory dir,
                                     @Nullable GoFile file,
                                     @Nullable String packageName,
                                     @NotNull MyScopeProcessor processor,
                                     @NotNull ResolveState state,
                                     boolean localProcessing) {
    String fileName = file != null ? file.getName() : null;
    if (dir != null) {
      for (PsiFile psiFile : dir.getFiles()) {
        if (psiFile instanceof GoFile && GoUtil.allowed(psiFile) && !psiFile.getName().equals(fileName)) {
          if (packageName != null && !packageName.equals(((GoFile)psiFile).getPackageName())) continue;
          if (!processFileEntities((GoFile)psiFile, processor, state, localProcessing)) return false;
        }
      }
    }
    return true;
  }

  private boolean processUnqualifiedResolve(@NotNull GoFile file,
                                            @NotNull MyScopeProcessor processor,
                                            @NotNull ResolveState state,
                                            boolean localResolve) {
    String id = getName();
    if ("_".equals(id)) return processSelf(processor, state);

    PsiElement parent = myElement.getParent();

    if (parent instanceof GoSelectorExpr) {
      boolean result = processSelector((GoSelectorExpr)parent, processor, state, myElement);
      if (processor.isCompletion()) return result;
      if (!result) return false;
    }
    
    PsiElement grandPa = parent.getParent();
    if (grandPa instanceof GoSelectorExpr && !processSelector((GoSelectorExpr)grandPa, processor, state, parent)) return false;

    GoScopeProcessorBase delegate = createDelegate(processor);
    ResolveUtil.treeWalkUp(myElement, delegate);
    processReceiver(delegate);
    processFunctionParameters(delegate);
    Collection<? extends GoNamedElement> result = delegate.getVariants();
    if (!processNamedElements(processor, state, result, localResolve)) return false;

    if (!processFileEntities(file, processor, state, localResolve)) return false;

    PsiDirectory dir = file.getOriginalFile().getParent();
    if (!processDirectory(dir, file, file.getPackageName(), processor, state, true)) return false;

    if (RESERVED_NAMES.contains(id)) return processSelf(processor, state);

    for (PsiElement o : file.getImportMap().values()) {
      if (o instanceof GoImportSpec) {
        if (((GoImportSpec)o).getDot() != null) {
          PsiDirectory implicitDir = ((GoImportSpec)o).getImportString().resolve();
          boolean resolved = !processDirectory(implicitDir, file, null, processor, state, false);
          if (resolved && !processor.isCompletion()) {
            putIfAbsent(o, myElement);
          }
          if (resolved) return false;
        }
        else if (!processor.execute(o, state)) return false;
      }
      if (o instanceof GoImportString) {
        PsiDirectory resolve = ((GoImportString)o).resolve();
        if (resolve != null && !processor.execute(resolve, state)) return false;
      }
    }

    GoFile builtinFile = GoSdkUtil.findBuiltinFile(myElement);
    if (builtinFile != null && !processFileEntities(builtinFile, processor, state, true)) return false;
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

  @NotNull
  private GoVarProcessor createDelegate(@NotNull MyScopeProcessor processor) {
    return new GoVarProcessor(getName(), myElement, processor.isCompletion());
  }

  private static boolean processFileEntities(@NotNull GoFile file,
                                             @NotNull MyScopeProcessor processor,
                                             @NotNull ResolveState state,
                                             boolean localProcessing) {
    if (!processNamedElements(processor, state, file.getConsts(), localProcessing)) return false;
    if (!processNamedElements(processor, state, file.getVars(), localProcessing)) return false;
    if (!processNamedElements(processor, state, file.getFunctions(), localProcessing)) return false;
    if (!processNamedElements(processor, state, file.getTypes(), localProcessing)) return false;
    return true;
  }

  private static boolean processNamedElements(@NotNull PsiScopeProcessor processor,
                                              @NotNull ResolveState state,
                                              @NotNull Collection<? extends GoNamedElement> elements, boolean localResolve) {
    for (GoNamedElement definition : elements) {
      if ((definition.isPublic() || localResolve) && !processor.execute(definition, state)) return false;
    }
    return true;
  }

  private boolean processSelf(@NotNull MyScopeProcessor processor, @NotNull ResolveState state) {
    return processor.execute(myElement, state);
  }

  // todo: return boolean for better performance 
  private void processFunctionParameters(@NotNull GoScopeProcessorBase processor) {
    GoSignatureOwner signatureOwner = PsiTreeUtil.getParentOfType(myElement, GoSignatureOwner.class);
    while (signatureOwner != null && processSignatureOwner(signatureOwner, processor)) {
      signatureOwner = PsiTreeUtil.getParentOfType(signatureOwner, GoSignatureOwner.class);
    }
  }

  private boolean processSignatureOwner(@NotNull GoSignatureOwner o, @NotNull GoScopeProcessorBase processor) {
    GoSignature signature = o.getSignature();
    if (signature == null) return true;
    if (!signature.getParameters().processDeclarations(processor, ResolveState.initial(), null, myElement)) return false;
    GoResult result = signature.getResult();
    GoParameters resultParameters = result != null ? result.getParameters() : null;
    if (resultParameters != null) return resultParameters.processDeclarations(processor, ResolveState.initial(), null, myElement);
    return true;
  }

  private void processReceiver(@NotNull GoScopeProcessorBase processor) {
    GoMethodDeclaration method = PsiTreeUtil.getParentOfType(myElement, GoMethodDeclaration.class);
    GoReceiver receiver = method != null ? method.getReceiver() : null;
    if (receiver != null) receiver.processDeclarations(processor, ResolveState.initial(), null, myElement);
  }

  @NotNull
  @Override
  public PsiElement handleElementRename(@NotNull String newElementName) throws IncorrectOperationException {
    myElement.getIdentifier().replace(GoElementFactory.createIdentifierFromText(myElement.getProject(), newElementName));
    return myElement;
  }
  
  static void putIfAbsent(@NotNull PsiElement importElement, @NotNull PsiElement usage) {
    List<PsiElement> list = importElement.getUserData(IMPORT_USERS);
    if (list == null) list = ContainerUtil.newArrayListWithCapacity(1);
    list.add(usage);
    importElement.putUserData(IMPORT_USERS, list);
  }  
}