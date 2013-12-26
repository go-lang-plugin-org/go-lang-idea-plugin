// This is a generated file. Not intended for manual editing.
package com.goide.psi.impl;

import com.goide.psi.GoFunction;
import com.goide.psi.GoFunctionLit;
import com.goide.psi.GoVisitor;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import org.jetbrains.annotations.NotNull;

import static com.goide.GoTypes.FUNC;

public class GoFunctionLitImpl extends GoCompositeElementImpl implements GoFunctionLit {

  public GoFunctionLitImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof GoVisitor) ((GoVisitor)visitor).visitFunctionLit(this);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public GoFunction getFunction() {
    return findNotNullChildByClass(GoFunction.class);
  }

  @Override
  @NotNull
  public PsiElement getFunc() {
    return findNotNullChildByType(FUNC);
  }

}
