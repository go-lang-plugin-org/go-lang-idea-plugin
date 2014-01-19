// This is a generated file. Not intended for manual editing.
package com.goide.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface GoExprSwitchStatement extends GoSwitchStatement {

  @NotNull
  List<GoExprCaseClause> getExprCaseClauseList();

  @Nullable
  GoExpression getExpression();

  @Nullable
  GoSimpleStatement getSimpleStatement();

  @NotNull
  GoSwitchStart getSwitchStart();

  @NotNull
  PsiElement getLbrace();

  @Nullable
  PsiElement getRbrace();

  @Nullable
  PsiElement getSemicolon();

}
