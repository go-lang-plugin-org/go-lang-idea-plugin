package com.goide.psi.impl;

import com.goide.psi.*;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class GoTypeReference extends GoReferenceBase {
  @NotNull private final PsiElement myIdentifier;
  @NotNull private final GoTypeReferenceExpression myRefExpression;

  public GoTypeReference(@NotNull GoTypeReferenceExpression element) {
    super(element, TextRange.from(element.getIdentifier().getStartOffsetInParent(), element.getIdentifier().getTextLength()));
    myIdentifier = element.getIdentifier();
    myRefExpression = element;
  }

  @Nullable
  @Override
  protected PsiElement getQualifier() {
    return myRefExpression.getQualifier();
  }

  @Nullable
  @Override
  protected PsiElement processUnqualified(@NotNull GoFile file, boolean localResolve) {
    String id = myIdentifier.getText();
    GoScopeProcessorBase processor = createProcessor(false);
    ResolveUtil.treeWalkUp(myRefExpression, processor);
    GoNamedElement result = processor.getResult();
    if (result != null) return result;
    for (GoTypeSpec t : file.getTypes()) { // todo: copy from completion or create a separate inspection
      if ((t.isPublic() || localResolve) && id.equals(t.getName())) return t;
    }
    return resolveImportOrPackage(file, id);
  }

  @Override
  protected void processFile(@NotNull List<LookupElement> result, @NotNull GoFile file, boolean localCompletion) {
    GoScopeProcessorBase processor = createProcessor(true);
    ResolveUtil.treeWalkUp(myRefExpression, processor);
    for (GoNamedElement element : processor.getVariants()) {
      if (element instanceof GoTypeSpec) {
        result.add(GoPsiImplUtil.createTypeLookupElement((GoTypeSpec)element));
      }
    }
    boolean insideInterfaceType = myElement.getParent() instanceof GoMethodSpec;
    for (GoTypeSpec t : file.getTypes()) {
      if (insideInterfaceType && !(t.getType() instanceof GoInterfaceType)) continue;
      if (t.isPublic() || localCompletion) {
        result.add(GoPsiImplUtil.createTypeLookupElement(t));
      }
    }
    processImports(result, file, localCompletion);
  }

  @NotNull
  @Override
  public PsiElement getIdentifier() {
    return myIdentifier;
  }

  @NotNull
  @Override
  protected GoScopeProcessorBase createProcessor(boolean completion) {
    return new GoTypeProcessor(myIdentifier.getText(), myRefExpression, completion);
  }
}
