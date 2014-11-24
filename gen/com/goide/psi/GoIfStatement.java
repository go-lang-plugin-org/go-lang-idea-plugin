// This is a generated file. Not intended for manual editing.
package com.goide.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface GoIfStatement extends GoStatement {

  @NotNull
  List<GoBlock> getBlockList();

  @Nullable
  GoExpression getExpression();

  @Nullable
  GoIfStatement getIfStatement();

  @Nullable
  GoStatement getStatement();

  @Nullable
  PsiElement getSemicolon();

  @Nullable
  PsiElement getElse();

  @NotNull
  PsiElement getIf();

}
