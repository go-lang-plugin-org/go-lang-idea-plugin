// This is a generated file. Not intended for manual editing.
package com.goide.psi;

import com.goide.stubs.GoFunctionDeclarationStub;
import com.intellij.psi.PsiElement;
import com.intellij.psi.StubBasedPsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface GoFunctionDeclaration extends GoNamedElement, StubBasedPsiElement<GoFunctionDeclarationStub> {

  @Nullable
  GoBlock getBlock();

  @Nullable
  GoSignature getSignature();

  @NotNull
  PsiElement getFunc();

  @NotNull
  PsiElement getIdentifier();

}
