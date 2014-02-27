// This is a generated file. Not intended for manual editing.
package com.goide.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

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
