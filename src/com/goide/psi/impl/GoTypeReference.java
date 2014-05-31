package com.goide.psi.impl;

import com.goide.psi.*;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.resolve.ResolveCache;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.util.ArrayUtil;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.containers.OrderedSet;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

public class GoTypeReference extends PsiPolyVariantReferenceBase<GoTypeReferenceExpression> {
  private final boolean myInsideInterfaceType;

  public GoTypeReference(@NotNull GoTypeReferenceExpression o) {
    super(o, TextRange.from(o.getIdentifier().getStartOffsetInParent(), o.getIdentifier().getTextLength()));
    myInsideInterfaceType = myElement.getParent() instanceof GoMethodSpec;
  }

  private static final ResolveCache.PolyVariantResolver<PsiPolyVariantReferenceBase> MY_RESOLVER =
    new ResolveCache.PolyVariantResolver<PsiPolyVariantReferenceBase>() {
      @NotNull
      @Override
      public ResolveResult[] resolve(@NotNull PsiPolyVariantReferenceBase psiPolyVariantReferenceBase, boolean incompleteCode) {
        return ((GoTypeReference)psiPolyVariantReferenceBase).resolveInner();
      }
    };

  @NotNull
  private ResolveResult[] resolveInner() {
    String identifierText = getName();
    Collection<ResolveResult> result = new OrderedSet<ResolveResult>();
    processResolveVariants(GoReference.createResolveProcessor(identifierText, result, myElement));
    return result.toArray(new ResolveResult[result.size()]);
  }

  private String getName() {
    return myElement.getIdentifier().getText();
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
    processResolveVariants(GoReference.createCompletionProcessor(variants, true));
    return ArrayUtil.toObjectArray(variants);
  }

  private boolean processResolveVariants(@NotNull GoReference.MyScopeProcessor processor) {
    PsiFile file = myElement.getContainingFile();
    if (!(file instanceof GoFile)) return false;
    ResolveState state = ResolveState.initial();
    GoTypeReferenceExpression qualifier = myElement.getQualifier();
    if (qualifier != null) {
      return processQualifierExpression(((GoFile)file), qualifier, processor, state);
    }
    return processUnqualifiedResolve(((GoFile)file), processor, state, true);
  }

  private static boolean processQualifierExpression(@NotNull GoFile file,
                                                    @NotNull GoTypeReferenceExpression qualifier,
                                                    @NotNull GoReference.MyScopeProcessor processor,
                                                    @NotNull ResolveState state) {
    PsiElement target = qualifier.getReference().resolve();
    if (target == null || target == qualifier) return false;
    if (target instanceof GoImportSpec) target = ((GoImportSpec)target).getImportString().resolve();
    if (target instanceof PsiDirectory) {
      GoReference.processDirectory((PsiDirectory)target, file, null, processor, state, false);
    }
    return false;
  }

  private boolean processUnqualifiedResolve(@NotNull GoFile file,
                                            @NotNull GoReference.MyScopeProcessor processor,
                                            @NotNull ResolveState state,
                                            boolean localResolve) {
    GoScopeProcessorBase delegate = createDelegate(processor);
    ResolveUtil.treeWalkUp(myElement, delegate);
    Collection<? extends GoNamedElement> result = delegate.getVariants();
    if (!processNamedElements(processor, state, result, localResolve)) return false;
    if (!processFileEntities(file, processor, state, localResolve)) return false;
    PsiDirectory dir = file.getOriginalFile().getParent();
    if (!GoReference.processDirectory(dir, file, file.getPackageName(), processor, state, true)) return false;
    if (GoReference.processImports(file, processor, state, myElement)) return false;
    if (GoReference.processBuiltin(processor, state, myElement)) return false;
    return true;
  }

  @NotNull
  private GoTypeProcessor createDelegate(@NotNull GoReference.MyScopeProcessor processor) {
    return new GoTypeProcessor(getName(), myElement, processor.isCompletion());
  }

  private boolean processFileEntities(@NotNull GoFile file,
                                      @NotNull GoReference.MyScopeProcessor processor,
                                      @NotNull ResolveState state,
                                      boolean localProcessing) {
    if (!processNamedElements(processor, state, file.getTypes(), localProcessing)) return false;
    return true;
  }

  private boolean processNamedElements(@NotNull PsiScopeProcessor processor,
                                      @NotNull ResolveState state,
                                      @NotNull Collection<? extends GoNamedElement> elements, boolean localResolve) {
    for (GoNamedElement definition : elements) {
      if (myInsideInterfaceType && definition instanceof GoTypeSpec && !(((GoTypeSpec)definition).getType() instanceof GoInterfaceType)) continue;
      if ((definition.isPublic() || localResolve) && !processor.execute(definition, state)) return false;
    }
    return true;
  }

  @Override
  public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException {
    myElement.getIdentifier().replace(GoElementFactory.createIdentifierFromText(myElement.getProject(), newElementName));
    return myElement;
  }
}
