// This is a generated file. Not intended for manual editing.
package com.goide.psi.impl;

import com.goide.psi.GoExpression;
import com.goide.psi.GoGoStatement;
import com.goide.psi.GoVisitor;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import org.jetbrains.annotations.NotNull;

import static com.goide.GoTypes.GO;

public class GoGoStatementImpl extends GoCompositeElementImpl implements GoGoStatement {

  public GoGoStatementImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof GoVisitor) ((GoVisitor)visitor).visitGoStatement(this);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public GoExpression getExpression() {
    return findNotNullChildByClass(GoExpression.class);
  }

  @Override
  @NotNull
  public PsiElement getGo() {
    return findNotNullChildByType(GO);
  }

}
