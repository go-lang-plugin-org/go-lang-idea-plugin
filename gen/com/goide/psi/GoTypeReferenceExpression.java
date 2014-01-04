// This is a generated file. Not intended for manual editing.
package com.goide.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;

public interface GoTypeReferenceExpression extends GoCompositeElement {

  @NotNull
  PsiElement getIdentifier();

  @Nullable
  PsiReference getReference();

  @Nullable
  GoTypeReferenceExpression getQualifier();

}
