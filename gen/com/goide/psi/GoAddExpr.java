// This is a generated file. Not intended for manual editing.
package com.goide.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface GoAddExpr extends GoBinaryExpr {

  @NotNull
  List<GoExpression> getExpressionList();

  @Nullable
  PsiElement getBitOr();

  @Nullable
  PsiElement getBitXor();

  @Nullable
  PsiElement getMinus();

  @Nullable
  PsiElement getPlus();

}
