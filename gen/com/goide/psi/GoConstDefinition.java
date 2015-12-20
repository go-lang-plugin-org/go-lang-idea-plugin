// This is a generated file. Not intended for manual editing.
package com.goide.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.StubBasedPsiElement;
import com.goide.stubs.GoConstDefinitionStub;
import com.intellij.psi.ResolveState;

public interface GoConstDefinition extends GoNamedElement, StubBasedPsiElement<GoConstDefinitionStub> {

  @NotNull
  PsiElement getIdentifier();

  @Nullable
  GoType getGoTypeInner(ResolveState context);

  @Nullable
  GoExpression getValue();

}
