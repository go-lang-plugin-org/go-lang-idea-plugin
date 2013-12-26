// This is a generated file. Not intended for manual editing.
package com.goide.psi.impl;

import com.goide.psi.GoDeferStatement;
import com.goide.psi.GoExpression;
import com.goide.psi.GoVisitor;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import org.jetbrains.annotations.NotNull;

import static com.goide.GoTypes.DEFER;

public class GoDeferStatementImpl extends GoCompositeElementImpl implements GoDeferStatement {

  public GoDeferStatementImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof GoVisitor) ((GoVisitor)visitor).visitDeferStatement(this);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public GoExpression getExpression() {
    return findNotNullChildByClass(GoExpression.class);
  }

  @Override
  @NotNull
  public PsiElement getDefer() {
    return findNotNullChildByType(DEFER);
  }

}
