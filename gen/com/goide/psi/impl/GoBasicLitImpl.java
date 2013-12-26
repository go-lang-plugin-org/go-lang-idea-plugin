// This is a generated file. Not intended for manual editing.
package com.goide.psi.impl;

import com.goide.psi.GoBasicLit;
import com.goide.psi.GoVisitor;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.goide.GoTypes.*;

public class GoBasicLitImpl extends GoCompositeElementImpl implements GoBasicLit {

  public GoBasicLitImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof GoVisitor) ((GoVisitor)visitor).visitBasicLit(this);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public PsiElement getDecimalI() {
    return findChildByType(DECIMAL_I);
  }

  @Override
  @Nullable
  public PsiElement getFloat() {
    return findChildByType(FLOAT);
  }

  @Override
  @Nullable
  public PsiElement getFloatI() {
    return findChildByType(FLOAT_I);
  }

  @Override
  @Nullable
  public PsiElement getImaginary() {
    return findChildByType(IMAGINARY);
  }

  @Override
  @Nullable
  public PsiElement getInt() {
    return findChildByType(INT);
  }

  @Override
  @Nullable
  public PsiElement getRune() {
    return findChildByType(RUNE);
  }

  @Override
  @Nullable
  public PsiElement getString() {
    return findChildByType(STRING);
  }

}
