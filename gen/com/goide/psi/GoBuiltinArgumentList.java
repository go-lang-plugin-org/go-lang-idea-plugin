// This is a generated file. Not intended for manual editing.
package com.goide.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface GoBuiltinArgumentList extends GoArgumentList {

  @NotNull
  List<GoExpression> getExpressionList();

  @Nullable
  GoType getType();

  @NotNull
  PsiElement getLparen();

  @Nullable
  PsiElement getRparen();

  @Nullable
  PsiElement getTripleDot();

}
