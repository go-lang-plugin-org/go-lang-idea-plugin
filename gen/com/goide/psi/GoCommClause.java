// This is a generated file. Not intended for manual editing.
package com.goide.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface GoCommClause extends GoCompositeElement {

  @NotNull
  GoCommCase getCommCase();

  @NotNull
  List<GoStatement> getStatementList();

  @Nullable
  PsiElement getColon();

}
