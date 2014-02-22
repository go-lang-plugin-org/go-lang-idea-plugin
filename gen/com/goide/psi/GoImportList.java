// This is a generated file. Not intended for manual editing.
package com.goide.psi;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface GoImportList extends GoCompositeElement {

  @NotNull
  List<GoImportDeclaration> getImportDeclarationList();

  @NotNull
  GoImportSpec addImport(String packagePath, String alias);

}
