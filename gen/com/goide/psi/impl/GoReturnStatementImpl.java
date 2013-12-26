// This is a generated file. Not intended for manual editing.
package com.goide.psi.impl;

import com.goide.psi.GoExpressionList;
import com.goide.psi.GoReturnStatement;
import com.goide.psi.GoVisitor;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.goide.GoTypes.RETURN;

public class GoReturnStatementImpl extends GoCompositeElementImpl implements GoReturnStatement {

  public GoReturnStatementImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof GoVisitor) ((GoVisitor)visitor).visitReturnStatement(this);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public GoExpressionList getExpressionList() {
    return findChildByClass(GoExpressionList.class);
  }

  @Override
  @NotNull
  public PsiElement getReturn() {
    return findNotNullChildByType(RETURN);
  }

}
