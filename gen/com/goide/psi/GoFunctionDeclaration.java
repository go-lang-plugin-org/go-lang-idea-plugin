// This is a generated file. Not intended for manual editing.
package com.goide.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface GoFunctionDeclaration extends GoTopLevelDeclaration {

  @Nullable
  GoBlock getBlock();

  @Nullable
  GoSignature getSignature();

  @NotNull
  PsiElement getFunc();

  @NotNull
  PsiElement getIdentifier();

}
