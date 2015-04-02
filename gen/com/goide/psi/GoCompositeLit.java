// This is a generated file. Not intended for manual editing.
package com.goide.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface GoCompositeLit extends GoExpression {

  @NotNull
  GoLiteralValue getLiteralValue();

  @Nullable
  GoType getType();

  @Nullable
  GoTypeReferenceExpression getTypeReferenceExpression();

}
