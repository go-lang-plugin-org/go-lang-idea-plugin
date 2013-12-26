// This is a generated file. Not intended for manual editing.
package com.goide.psi.impl;

import com.goide.psi.GoPrimaryExpr;
import com.goide.psi.GoTypeSwitchGuard;
import com.goide.psi.GoVisitor;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.goide.GoTypes.IDENTIFIER;
import static com.goide.GoTypes.TYPE;

public class GoTypeSwitchGuardImpl extends GoCompositeElementImpl implements GoTypeSwitchGuard {

  public GoTypeSwitchGuardImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof GoVisitor) ((GoVisitor)visitor).visitTypeSwitchGuard(this);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public GoPrimaryExpr getPrimaryExpr() {
    return findNotNullChildByClass(GoPrimaryExpr.class);
  }

  @Override
  @Nullable
  public PsiElement getIdentifier() {
    return findChildByType(IDENTIFIER);
  }

  @Override
  @NotNull
  public PsiElement getType() {
    return findNotNullChildByType(TYPE);
  }

}
