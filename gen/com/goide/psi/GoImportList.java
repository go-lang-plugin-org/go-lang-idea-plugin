// This is a generated file. Not intended for manual editing.
package com.goide.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface GoImportList extends GoCompositeElement {

  @NotNull
  List<GoImportDeclaration> getImportDeclarationList();

  @NotNull
  GoImportSpec addImport(String packagePath, String alias);

}
