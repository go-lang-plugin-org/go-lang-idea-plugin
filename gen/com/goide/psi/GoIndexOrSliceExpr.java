// This is a generated file. Not intended for manual editing.
package com.goide.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface GoIndexOrSliceExpr extends GoExpression {

  @NotNull
  List<GoExpression> getExpressionList();

  @NotNull
  PsiElement getLbrack();

  @Nullable
  PsiElement getRbrack();

}
