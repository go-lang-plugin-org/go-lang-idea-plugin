// This is a generated file. Not intended for manual editing.
package com.goide.psi.impl;

import com.goide.psi.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.goide.GoTypes.FOR;

public class GoForStatementImpl extends GoCompositeElementImpl implements GoForStatement {

  public GoForStatementImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof GoVisitor) ((GoVisitor)visitor).visitForStatement(this);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public GoBlock getBlock() {
    return findNotNullChildByClass(GoBlock.class);
  }

  @Override
  @Nullable
  public GoExpression getExpression() {
    return findChildByClass(GoExpression.class);
  }

  @Override
  @Nullable
  public GoForClause getForClause() {
    return findChildByClass(GoForClause.class);
  }

  @Override
  @Nullable
  public GoRangeClause getRangeClause() {
    return findChildByClass(GoRangeClause.class);
  }

  @Override
  @NotNull
  public PsiElement getFor() {
    return findNotNullChildByType(FOR);
  }

}
