// This is a generated file. Not intended for manual editing.
package com.goide.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface GoSendStatement extends GoStatement {

  @NotNull
  List<GoExpression> getExpressionList();

  @Nullable
  GoLeftHandExprList getLeftHandExprList();

  @NotNull
  PsiElement getSendChannel();

}
