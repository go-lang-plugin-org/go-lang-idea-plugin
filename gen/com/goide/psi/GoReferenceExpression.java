// This is a generated file. Not intended for manual editing.
package com.goide.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;

public interface GoReferenceExpression extends GoExpression {

  @NotNull
  PsiElement getIdentifier();

  @NotNull
  com.goide.psi.impl.GoReference getReference();

  @Nullable
  GoReferenceExpression getQualifier();

}
