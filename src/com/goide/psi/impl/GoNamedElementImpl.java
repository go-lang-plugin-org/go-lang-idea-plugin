package com.goide.psi.impl;

import com.goide.psi.GoCompositeElement;
import com.goide.psi.GoNamedElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public abstract class GoNamedElementImpl extends GoCompositeElementImpl implements GoCompositeElement, GoNamedElement {
  public GoNamedElementImpl(ASTNode node) {
    super(node);
  }

  @Override
  public PsiElement setName(@NonNls @NotNull String s) throws IncorrectOperationException {
    return this;
  }
}
