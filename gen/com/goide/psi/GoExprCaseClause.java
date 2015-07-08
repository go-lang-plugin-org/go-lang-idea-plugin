// This is a generated file. Not intended for manual editing.
package com.goide.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface GoExprCaseClause extends GoCaseClause {

  @NotNull
  List<GoExpression> getExpressionList();

  @NotNull
  List<GoStatement> getStatementList();

  @Nullable
  PsiElement getColon();

  @Nullable
  PsiElement getCase();

  @Nullable
  PsiElement getDefault();

}
