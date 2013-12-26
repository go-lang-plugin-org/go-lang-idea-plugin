// This is a generated file. Not intended for manual editing.
package com.goide.psi.impl;

import com.goide.psi.GoBreakStatement;
import com.goide.psi.GoLabel;
import com.goide.psi.GoVisitor;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.goide.GoTypes.BREAK;

public class GoBreakStatementImpl extends GoCompositeElementImpl implements GoBreakStatement {

  public GoBreakStatementImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof GoVisitor) ((GoVisitor)visitor).visitBreakStatement(this);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public GoLabel getLabel() {
    return findChildByClass(GoLabel.class);
  }

  @Override
  @NotNull
  public PsiElement getBreak() {
    return findNotNullChildByType(BREAK);
  }

}
