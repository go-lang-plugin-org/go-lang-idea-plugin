package com.goide.psi;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface GoFunctionOrMethodDeclaration extends GoNamedElement, GoTopLevelDeclaration, GoSignatureOwner {

  @Nullable
  GoBlock getBlock();

  @Nullable
  GoSignature getSignature();

  @NotNull
  PsiElement getFunc();

  @NotNull
  PsiElement getIdentifier();
}
