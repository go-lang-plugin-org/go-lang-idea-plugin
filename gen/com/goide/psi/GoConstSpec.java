// This is a generated file. Not intended for manual editing.
package com.goide.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.StubBasedPsiElement;
import com.goide.stubs.GoConstSpecStub;

public interface GoConstSpec extends GoCompositeElement, StubBasedPsiElement<GoConstSpecStub> {

  @NotNull
  List<GoConstDefinition> getConstDefinitionList();

  @NotNull
  List<GoExpression> getExpressionList();

  @Nullable
  GoType getType();

  @Nullable
  PsiElement getAssign();

  void deleteDefinition(GoConstDefinition definitionToDelete);

}
