// This is a generated file. Not intended for manual editing.
package com.goide.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface GoStatement extends GoCompositeElement {

  @Nullable
  GoBlock getBlock();

  @Nullable
  GoBreakStatement getBreakStatement();

  @Nullable
  GoContinueStatement getContinueStatement();

  @Nullable
  GoDeclarationStatement getDeclarationStatement();

  @Nullable
  GoDeferStatement getDeferStatement();

  @Nullable
  GoFallthroughStatement getFallthroughStatement();

  @Nullable
  GoForStatement getForStatement();

  @Nullable
  GoGoStatement getGoStatement();

  @Nullable
  GoGotoStatement getGotoStatement();

  @Nullable
  GoIfStatement getIfStatement();

  @Nullable
  GoLabeledStatement getLabeledStatement();

  @Nullable
  GoReturnStatement getReturnStatement();

  @Nullable
  GoSelectStatement getSelectStatement();

  @Nullable
  GoSimpleStatement getSimpleStatement();

  @Nullable
  GoSwitchStatement getSwitchStatement();

}
