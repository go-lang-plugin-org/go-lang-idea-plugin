// This is a generated file. Not intended for manual editing.
package com.goide.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface GoTypeSwitchGuard extends GoCompositeElement {

  @NotNull
  GoExpression getExpression();

  @NotNull
  GoTypeGuard getTypeGuard();

  @Nullable
  GoVarDefinition getVarDefinition();

  @NotNull
  PsiElement getDot();

  @Nullable
  PsiElement getVarAssign();

}
