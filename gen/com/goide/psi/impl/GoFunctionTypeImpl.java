// This is a generated file. Not intended for manual editing.
package com.goide.psi.impl;

import com.goide.psi.GoFunctionType;
import com.goide.psi.GoSignature;
import com.goide.psi.GoVisitor;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import org.jetbrains.annotations.NotNull;

import static com.goide.GoTypes.FUNC;

public class GoFunctionTypeImpl extends GoCompositeElementImpl implements GoFunctionType {

  public GoFunctionTypeImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof GoVisitor) ((GoVisitor)visitor).visitFunctionType(this);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public GoSignature getSignature() {
    return findNotNullChildByClass(GoSignature.class);
  }

  @Override
  @NotNull
  public PsiElement getFunc() {
    return findNotNullChildByType(FUNC);
  }

}
