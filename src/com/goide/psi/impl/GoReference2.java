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
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.containers.OrderedSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public class GoReference2 extends PsiPolyVariantReferenceBase<GoReferenceExpression> {
  private static final Set<String> RESERVED_NAMES = ContainerUtil.newHashSet("print", "println");

  private static final ResolveCache.PolyVariantResolver<PsiPolyVariantReferenceBase> MY_RESOLVER =
    new ResolveCache.PolyVariantResolver<PsiPolyVariantReferenceBase>() {
      @NotNull
      @Override
      public ResolveResult[] resolve(@NotNull PsiPolyVariantReferenceBase psiPolyVariantReferenceBase, boolean incompleteCode) {
        return ((GoReference2)psiPolyVariantReferenceBase).resolveInner();
      }
    };

  public GoReference2(GoReferenceExpression element) {
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
      return false;
    }
    return processUnqualifiedResolve(((GoFile)file), processor, state, true);
  }

  private boolean processUnqualifiedResolve(GoFile file, MyScopeProcessor processor, ResolveState state, boolean localResolve) {
    String id = getName();
    if ("_".equals(id)) return processSelf(processor, state);

    GoScopeProcessorBase delegate = new GoVarProcessor(id, myElement, processor.isCompletion());
    ResolveUtil.treeWalkUp(myElement, delegate);
    processReceiver(delegate);
    processFunctionParameters(delegate);
    GoNamedElement result = delegate.getResult();
    if (result != null && !processor.execute(result, state)) return false;

    processFile(file, processor, state, localResolve);

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
      processFile(builtinFile, processor, state, true);
      List<GoTypeSpec> types = builtinFile.getTypes();
      processNamedElements(processor, state, true, types); // todo: remove ComplexType and others
    }

    return false;
  }

  private static void processFile(GoFile file, MyScopeProcessor processor, ResolveState state, boolean localResolve) {
    processNamedElements(processor, state, localResolve, file.getConsts());
    processNamedElements(processor, state, localResolve, file.getVars());
    processNamedElements(processor, state, localResolve, file.getFunctions());
  }

  private static void processNamedElements(PsiScopeProcessor processor,
                                           ResolveState state,
                                           boolean localResolve,
                                           List<? extends GoNamedElement> elements) {
    for (GoNamedElement definition : elements) {
      if (definition.isPublic() || localResolve) processor.execute(definition, state);
    }
  }

  private boolean processSelf(PsiScopeProcessor processor, ResolveState state) {
    return processor.execute(myElement, state);
  }

  //@Nullable
  //@Override
  //protected PsiElement processUnqualified(@NotNull GoFile file, boolean localResolve) {
  //  String id = myElement.getIdentifier().getText();
  //  if ("_".equals(id)) return myElement; // todo: need a better solution

  //  PsiElement parent = myElement.getParent();
  //  if (parent instanceof GoSelectorExpr) {
  //    List<GoExpression> list = ((GoSelectorExpr)parent).getExpressionList();
  //    if (list.size() > 1 && list.get(1).isEquivalentTo(myElement)) {
  //      GoType type = list.get(0).getGoType();
  //      PsiElement element = processGoType(type);
  //      if (element != null) return element;
  //      return null;
  //    }
  //  }
  //  // todo: remove duplicate
  //  PsiElement grandPa = parent.getParent();
  //  if (grandPa instanceof GoSelectorExpr) {
  //    List<GoExpression> list = ((GoSelectorExpr)grandPa).getExpressionList();
  //    if (list.size() > 1 && list.get(1).isEquivalentTo(parent)) {
  //      GoType type = list.get(0).getGoType();
  //      PsiElement element = processGoType(type);
  //      if (element != null) return element;
  //    }
  //  }
  //
  //  if (myElement.getParent() instanceof GoCallExpr && StringUtil.toLowerCase(id).equals(id)) {
  //    GoFile builtinFile = GoSdkUtil.findBuiltinFile(myElement);
  //    if (builtinFile != null) {
  //      List<GoTypeSpec> types = builtinFile.getTypes();
  //      for (GoTypeSpec type : types) {
  //        if (id.equals(type.getName())) return type;
  //      }
  //    }
  //  }
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
}
