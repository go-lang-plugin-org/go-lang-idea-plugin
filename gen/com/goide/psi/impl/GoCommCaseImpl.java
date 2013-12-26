// This is a generated file. Not intended for manual editing.
package com.goide.psi.impl;

import com.goide.psi.GoCommCase;
import com.goide.psi.GoRecvStatement;
import com.goide.psi.GoSendStatement;
import com.goide.psi.GoVisitor;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.goide.GoTypes.CASE;
import static com.goide.GoTypes.DEFAULT;

public class GoCommCaseImpl extends GoCompositeElementImpl implements GoCommCase {

  public GoCommCaseImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof GoVisitor) ((GoVisitor)visitor).visitCommCase(this);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public GoRecvStatement getRecvStatement() {
    return findChildByClass(GoRecvStatement.class);
  }

  @Override
  @Nullable
  public GoSendStatement getSendStatement() {
    return findChildByClass(GoSendStatement.class);
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
