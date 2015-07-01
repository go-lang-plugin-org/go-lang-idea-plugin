// This is a generated file. Not intended for manual editing.
package com.goide.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface GoTypeCaseClause extends GoCompositeElement {

  @NotNull
  List<GoStatement> getStatementList();

  @Nullable
  GoTypeSwitchCase getTypeSwitchCase();

  @Nullable
  PsiElement getColon();

}
