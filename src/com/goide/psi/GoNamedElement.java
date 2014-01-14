package com.goide.psi;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;
import org.jetbrains.annotations.Nullable;

public interface GoNamedElement extends GoCompositeElement, PsiNameIdentifierOwner {
  boolean isPublic();

  @Nullable
  PsiElement getIdentifier();

  @Nullable
  GoType getGoType();
}
