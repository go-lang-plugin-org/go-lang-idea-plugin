// This is a generated file. Not intended for manual editing.
package com.goide.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface GoIfStatement extends GoStatement {

  @Nullable
  GoBlock getBlock();

  @Nullable
  GoElseStatement getElseStatement();

  @Nullable
  GoExpression getExpression();

  @Nullable
  GoStatement getStatement();

  @Nullable
  PsiElement getSemicolon();

  @NotNull
  PsiElement getIf();

}
