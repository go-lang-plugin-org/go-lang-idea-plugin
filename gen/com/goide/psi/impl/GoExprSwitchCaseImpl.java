// This is a generated file. Not intended for manual editing.
package com.goide.psi.impl;

import com.goide.psi.GoExprSwitchCase;
import com.goide.psi.GoExpressionList;
import com.goide.psi.GoVisitor;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.goide.GoTypes.CASE;
import static com.goide.GoTypes.DEFAULT;

public class GoExprSwitchCaseImpl extends GoCompositeElementImpl implements GoExprSwitchCase {

  public GoExprSwitchCaseImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof GoVisitor) ((GoVisitor)visitor).visitExprSwitchCase(this);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public GoExpressionList getExpressionList() {
    return findChildByClass(GoExpressionList.class);
  }

  @Override
  @Nullable
  public PsiElement getCase() {
    return findChildByType(CASE);
  }

  @Override
  @Nullable
  public PsiElement getDefault() {
    return findChildByType(DEFAULT);
  }

}
