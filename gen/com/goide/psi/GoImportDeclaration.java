// This is a generated file. Not intended for manual editing.
package com.goide.psi;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface GoImportDeclaration extends GoCompositeElement {

  @NotNull
  List<GoImportSpec> getImportSpecList();

  @Nullable
  PsiElement getLparen();

  @Nullable
  PsiElement getRparen();

  @NotNull
  PsiElement getImport();

  @NotNull
  GoImportSpec addImportSpec(String packagePath, String alias);

}
