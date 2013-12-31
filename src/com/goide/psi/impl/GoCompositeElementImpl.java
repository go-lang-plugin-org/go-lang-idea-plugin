package com.goide.psi.impl;

import com.goide.psi.GoCompositeElement;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;

public class GoCompositeElementImpl extends ASTWrapperPsiElement implements GoCompositeElement {
  public GoCompositeElementImpl(@NotNull ASTNode node) {
    super(node);
  }

  @Override
  public String toString() {
    return getNode().getElementType().toString();
  }
}
