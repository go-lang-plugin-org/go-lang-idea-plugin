// This is a generated file. Not intended for manual editing.
package com.goide.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static com.goide.GoTypes.*;
import com.goide.psi.*;

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
    return findChildByType(GO_DECIMAL_I);
  }

  @Override
  @Nullable
  public PsiElement getFloat() {
    return findChildByType(GO_FLOAT);
  }

  @Override
  @Nullable
  public PsiElement getFloatI() {
    return findChildByType(GO_FLOAT_I);
  }

  @Override
  @Nullable
  public PsiElement getImaginary() {
    return findChildByType(GO_IMAGINARY);
  }

  @Override
  @Nullable
  public PsiElement getInt() {
    return findChildByType(GO_INT);
  }

  @Override
  @Nullable
  public PsiElement getRune() {
    return findChildByType(GO_RUNE);
  }

  @Override
  @Nullable
  public PsiElement getString() {
    return findChildByType(GO_STRING);
  }

}
