// This is a generated file. Not intended for manual editing.
package com.goide.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.StubBasedPsiElement;
import com.goide.stubs.GoFunctionDeclarationStub;

public interface GoFunctionDeclaration extends GoFunctionOrMethodDeclaration, StubBasedPsiElement<GoFunctionDeclarationStub> {

  @Nullable
  GoBlock getBlock();

  @Nullable
  GoSignature getSignature();

  @NotNull
  PsiElement getFunc();

  @NotNull
  PsiElement getIdentifier();

}
