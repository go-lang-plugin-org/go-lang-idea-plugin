// This is a generated file. Not intended for manual editing.
package com.goide.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface GoFunctionDecl extends GoCompositeElement {

  @Nullable
  GoFunction getFunction();

  @NotNull
  GoFunctionName getFunctionName();

  @Nullable
  GoSignature getSignature();

  @NotNull
  PsiElement getFunc();

}
