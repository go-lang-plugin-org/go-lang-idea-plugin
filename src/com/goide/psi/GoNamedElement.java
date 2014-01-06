package com.goide.psi;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface GoNamedElement extends GoCompositeElement, PsiNameIdentifierOwner {
  @Nullable
  PsiElement getIdentifier();
}
