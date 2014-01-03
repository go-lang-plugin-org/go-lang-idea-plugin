package com.goide.psi.impl;

import com.goide.psi.GoFunctionDeclaration;
import com.goide.psi.GoTopLevelDeclaration;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

public abstract class GoFunctionDeclImpl extends GoNamedElementImpl implements GoFunctionDeclaration, GoTopLevelDeclaration {
  public GoFunctionDeclImpl(ASTNode node) {
    super(node);
  }

  @NotNull
  @Override
  public PsiElement getNameIdentifier() {
    return getIdentifier();
  }

  @Override
  public String getName() {
    return getIdentifier().getText();
  }

  @Override
  public int getTextOffset() {
    return getIdentifier().getTextOffset();
  }
}
