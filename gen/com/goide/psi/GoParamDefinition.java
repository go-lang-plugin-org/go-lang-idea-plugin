// This is a generated file. Not intended for manual editing.
package com.goide.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.StubBasedPsiElement;
import com.goide.stubs.GoParamDefinitionStub;

public interface GoParamDefinition extends GoNamedElement, StubBasedPsiElement<GoParamDefinitionStub> {

  @NotNull
  PsiElement getIdentifier();

  @NotNull
  boolean isVariadic();

}
