package com.goide.psi;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;
import org.jetbrains.annotations.Nullable;

public interface GoNamedElement extends GoCompositeElement, GoTypeOwner, PsiNameIdentifierOwner {
  boolean isPublic();

  @Nullable
  PsiElement getIdentifier();
}
