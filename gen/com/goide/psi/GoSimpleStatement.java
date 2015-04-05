// This is a generated file. Not intended for manual editing.
package com.goide.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface GoSimpleStatement extends GoStatement {

  @Nullable
  GoLeftHandExprList getLeftHandExprList();

  @Nullable
  GoShortVarDeclaration getShortVarDeclaration();

  @Nullable
  GoStatement getStatement();

  @Nullable
  PsiElement getMinusMinus();

  @Nullable
  PsiElement getPlusPlus();

}
