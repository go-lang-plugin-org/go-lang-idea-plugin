// This is a generated file. Not intended for manual editing.
package com.goide.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.goide.psi.impl.GoReference;

public interface GoReferenceExpression extends GoExpression, GoReferenceExpressionBase {

  @NotNull
  PsiElement getIdentifier();

  @NotNull
  GoReference getReference();

  @Nullable
  GoReferenceExpression getQualifier();

}
