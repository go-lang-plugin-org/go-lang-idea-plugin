// This is a generated file. Not intended for manual editing.
package com.goide.psi.impl;

import com.goide.psi.GoFallthroughStatement;
import com.goide.psi.GoVisitor;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import org.jetbrains.annotations.NotNull;

import static com.goide.GoTypes.FALLTHROUGH;

public class GoFallthroughStatementImpl extends GoCompositeElementImpl implements GoFallthroughStatement {

  public GoFallthroughStatementImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof GoVisitor) ((GoVisitor)visitor).visitFallthroughStatement(this);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public PsiElement getFallthrough() {
    return findNotNullChildByType(FALLTHROUGH);
  }

}
