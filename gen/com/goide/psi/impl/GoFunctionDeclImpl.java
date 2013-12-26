// This is a generated file. Not intended for manual editing.
package com.goide.psi.impl;

import com.goide.psi.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.goide.GoTypes.FUNC;

public class GoFunctionDeclImpl extends GoCompositeElementImpl implements GoFunctionDecl {

  public GoFunctionDeclImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof GoVisitor) ((GoVisitor)visitor).visitFunctionDecl(this);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public GoFunction getFunction() {
    return findChildByClass(GoFunction.class);
  }

  @Override
  @NotNull
  public GoFunctionName getFunctionName() {
    return findNotNullChildByClass(GoFunctionName.class);
  }

  @Override
  @Nullable
  public GoSignature getSignature() {
    return findChildByClass(GoSignature.class);
  }

  @Override
  @NotNull
  public PsiElement getFunc() {
    return findNotNullChildByType(FUNC);
  }

}
