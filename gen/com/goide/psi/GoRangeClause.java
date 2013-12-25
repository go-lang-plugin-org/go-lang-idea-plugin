// This is a generated file. Not intended for manual editing.
package com.goide.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface GoRangeClause extends GoCompositeElement {

  @NotNull
  GoExpression getExpression();

  @Nullable
  GoExpressionList getExpressionList();

  @Nullable
  GoIdentifierList getIdentifierList();

  @NotNull
  PsiElement getRange();

}
