// This is a generated file. Not intended for manual editing.
package com.goide.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface GoElseStatement extends GoStatement {

  @Nullable
  GoBlock getBlock();

  @Nullable
  GoIfStatement getIfStatement();

  @NotNull
  PsiElement getElse();

}
