// This is a generated file. Not intended for manual editing.
package com.goide.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface GoInterfaceType extends GoType {

  @NotNull
  List<GoMethodSpec> getMethodSpecList();

  @Nullable
  PsiElement getLbrace();

  @Nullable
  PsiElement getRbrace();

  @NotNull
  PsiElement getInterface();

  @NotNull
  List<GoMethodSpec> getMethods();

  @NotNull
  List<GoTypeReferenceExpression> getBaseTypesReferences();

}
