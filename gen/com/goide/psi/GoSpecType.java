// This is a generated file. Not intended for manual editing.
package com.goide.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.StubBasedPsiElement;
import com.goide.stubs.GoTypeStub;

public interface GoSpecType extends GoType, StubBasedPsiElement<GoTypeStub> {

  @NotNull
  GoType getType();

  @NotNull
  PsiElement getIdentifier();

}
