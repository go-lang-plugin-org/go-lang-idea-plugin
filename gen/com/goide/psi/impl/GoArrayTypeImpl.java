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

public class GoArrayTypeImpl extends GoGoTypeImpl implements GoArrayType {

  public GoArrayTypeImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof GoVisitor) ((GoVisitor)visitor).visitArrayType(this);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public GoArrayLength getArrayLength() {
    return findNotNullChildByClass(GoArrayLength.class);
  }

  @Override
  @NotNull
  public GoGoType getGoType() {
    return findNotNullChildByClass(GoGoType.class);
  }

}
