// This is a generated file. Not intended for manual editing.
package com.goide.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface GoIfStatement extends GoCompositeElement {

  @NotNull
  List<GoBlock> getBlockList();

  @NotNull
  GoExpression getExpression();

  @Nullable
  GoIfStatement getIfStatement();

  @Nullable
  GoSimpleStatement getSimpleStatement();

  @Nullable
  PsiElement getElse();

  @NotNull
  PsiElement getIf();

}
