package com.goide.psi.impl;

import com.goide.psi.GoReferenceExpression;
import com.goide.psi.GoTypeReferenceExpression;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GoPsiImplUtil {
  @Nullable
  public static PsiReference getReference(@NotNull GoTypeReferenceExpression o) {
    return null;
  }

  @Nullable
  public static GoReferenceExpression getQualifier(@NotNull GoReferenceExpression o) {
    return PsiTreeUtil.getChildOfType(o, GoReferenceExpression.class);
  }

  @Nullable
  public static PsiReference getReference(@NotNull final GoReferenceExpression o) {
    PsiElement identifier = o.getIdentifier();
    return new PsiReferenceBase<PsiElement>(identifier, TextRange.from(0, identifier.getTextLength())) {
      @Nullable
      @Override
      public PsiElement resolve() {
        return null;
      }

      @NotNull
      @Override
      public Object[] getVariants() {
        return new Object[0];
      }
    };
  }
}
