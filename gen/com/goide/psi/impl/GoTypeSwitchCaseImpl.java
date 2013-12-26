// This is a generated file. Not intended for manual editing.
package com.goide.psi.impl;

import com.goide.psi.GoTypeList;
import com.goide.psi.GoTypeSwitchCase;
import com.goide.psi.GoVisitor;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.goide.GoTypes.CASE;
import static com.goide.GoTypes.DEFAULT;

public class GoTypeSwitchCaseImpl extends GoCompositeElementImpl implements GoTypeSwitchCase {

  public GoTypeSwitchCaseImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof GoVisitor) ((GoVisitor)visitor).visitTypeSwitchCase(this);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public GoTypeList getTypeList() {
    return findChildByClass(GoTypeList.class);
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
