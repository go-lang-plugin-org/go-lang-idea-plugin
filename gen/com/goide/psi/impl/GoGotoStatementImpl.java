// This is a generated file. Not intended for manual editing.
package com.goide.psi.impl;

import com.goide.psi.GoGotoStatement;
import com.goide.psi.GoLabel;
import com.goide.psi.GoVisitor;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import org.jetbrains.annotations.NotNull;

import static com.goide.GoTypes.GOTO;

public class GoGotoStatementImpl extends GoCompositeElementImpl implements GoGotoStatement {

  public GoGotoStatementImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof GoVisitor) ((GoVisitor)visitor).visitGotoStatement(this);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public GoLabel getLabel() {
    return findNotNullChildByClass(GoLabel.class);
  }

  @Override
  @NotNull
  public PsiElement getGoto() {
    return findNotNullChildByType(GOTO);
  }

}
