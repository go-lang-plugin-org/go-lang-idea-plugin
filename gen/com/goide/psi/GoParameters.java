// This is a generated file. Not intended for manual editing.
package com.goide.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.StubBasedPsiElement;
import com.goide.stubs.GoParametersStub;

public interface GoParameters extends GoCompositeElement, StubBasedPsiElement<GoParametersStub> {

  @NotNull
  List<GoParameterDeclaration> getParameterDeclarationList();

  @Nullable
  GoType getType();

  @NotNull
  PsiElement getLparen();

  @Nullable
  PsiElement getRparen();

}
