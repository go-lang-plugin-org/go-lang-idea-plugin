// This is a generated file. Not intended for manual editing.
package com.goide.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface GoSimpleStatement extends GoCompositeElement {

  @Nullable
  GoAssignmentStatement getAssignmentStatement();

  @Nullable
  GoExpressionStatement getExpressionStatement();

  @Nullable
  GoIncDecStatement getIncDecStatement();

  @Nullable
  GoSendStatement getSendStatement();

  @Nullable
  GoShortVarDecl getShortVarDecl();

}
