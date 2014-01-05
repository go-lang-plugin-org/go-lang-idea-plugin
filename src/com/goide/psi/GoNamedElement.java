package com.goide.psi;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;
import org.jetbrains.annotations.NotNull;

public interface GoNamedElement extends GoCompositeElement, PsiNameIdentifierOwner {
  @NotNull
  PsiElement getIdentifier();
}
