package com.goide.psi.impl;

import com.goide.psi.*;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReferenceBase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

class GoReference extends PsiReferenceBase<PsiElement> {
  @NotNull private final PsiElement myIdentifier;
  @NotNull private final GoReferenceExpression myRefExpression;

  public GoReference(@NotNull GoReferenceExpression refExpression) {
    super(refExpression.getIdentifier(), TextRange.from(0, refExpression.getIdentifier().getTextLength()));
    myIdentifier = refExpression.getIdentifier();
    myRefExpression = refExpression;
  }

  @Nullable
  @Override
  public PsiElement resolve() {
    GoReferenceExpression qualifier = myRefExpression.getQualifier();
    if (qualifier == null) {
      PsiFile file = myRefExpression.getContainingFile();
      if (file instanceof GoFile) {
        List<GoTopLevelDeclaration> declarations = ((GoFile)file).getDeclarations();
        for (GoTopLevelDeclaration declaration : declarations) {
          if (declaration instanceof GoFunctionDeclaration && !(declaration instanceof GoMethodDeclaration)) {
            if (myIdentifier.getText().equals(((GoFunctionDeclaration)declaration).getName())) {
              return declaration;
            }
          }
        }
      }
    }
    return null;
  }

  @NotNull
  @Override
  public Object[] getVariants() {
    return new Object[0];
  }
}
