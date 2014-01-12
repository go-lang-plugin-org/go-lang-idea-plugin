package com.goide.psi.impl;

import com.goide.psi.GoFile;
import com.goide.psi.GoTypeReferenceExpression;
import com.goide.psi.GoTypeSpec;
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

  @Override
  public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException {
    myRefExpression.replace(GoElementFactory.createTypeReferenceFromText(myElement.getProject(), newElementName));
    return myRefExpression;
  }

  @Nullable
  @Override
  protected PsiElement processUnqualified(@NotNull GoFile file, boolean localResolve) {
    String id = myIdentifier.getText();
    for (GoTypeSpec t : file.getTypes()) {
      if ((isPublic(t) || localResolve) && id.equals(t.getName())) return t;
    }
    return resolveImportOrPackage(file, id);
  }

  @Override
  protected void processFile(@NotNull List<LookupElement> result, @NotNull GoFile file, boolean localCompletion) {
    for (GoTypeSpec t : file.getTypes()) {
      if (isPublic(t) || localCompletion) result.add(GoPsiImplUtil.createTypeLookupElement(t));
    }
    processImports(result, file, localCompletion);
  }

  @NotNull
  @Override
  public PsiElement getIdentifier() {
    return myIdentifier;
  }
}
