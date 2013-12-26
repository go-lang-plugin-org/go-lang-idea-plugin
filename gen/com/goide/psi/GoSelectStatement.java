// This is a generated file. Not intended for manual editing.
package com.goide.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface GoSelectStatement extends GoStatement {

  @NotNull
  List<GoCommClause> getCommClauseList();

  @NotNull
  PsiElement getSelect();

}
