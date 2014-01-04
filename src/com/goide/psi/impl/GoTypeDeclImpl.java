package com.goide.psi.impl;

import com.goide.psi.GoTypeSpec;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

public abstract class GoTypeDeclImpl extends GoNamedElementImpl implements GoTypeSpec {
  public GoTypeDeclImpl(ASTNode node) {
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
