// This is a generated file. Not intended for manual editing.
package com.goide.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface GoRangeClause extends GoVarSpec {

  @NotNull
  List<GoExpression> getExpressionList();

  @NotNull
  List<GoVarDefinition> getVarDefinitionList();

  @Nullable
  PsiElement getAssign();

  @Nullable
  PsiElement getVarAssign();

  @NotNull
  PsiElement getRange();

}
