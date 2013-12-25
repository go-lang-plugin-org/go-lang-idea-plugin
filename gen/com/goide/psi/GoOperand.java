// This is a generated file. Not intended for manual editing.
package com.goide.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface GoOperand extends GoCompositeElement {

  @Nullable
  GoExpression getExpression();

  @Nullable
  GoLiteral getLiteral();

  @Nullable
  GoMethodExpr getMethodExpr();

  @Nullable
  GoOperandName getOperandName();

}
