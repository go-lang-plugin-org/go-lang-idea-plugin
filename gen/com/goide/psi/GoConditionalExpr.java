// This is a generated file. Not intended for manual editing.
package com.goide.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface GoConditionalExpr extends GoBinaryExpr {

  @NotNull
  List<GoExpression> getExpressionList();

  @Nullable
  PsiElement getEq();

  @Nullable
  PsiElement getGreater();

  @Nullable
  PsiElement getGreaterOrEqual();

  @Nullable
  PsiElement getLess();

  @Nullable
  PsiElement getLessOrEqual();

  @Nullable
  PsiElement getNotEq();

}
