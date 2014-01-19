// This is a generated file. Not intended for manual editing.
package com.goide.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.StubBasedPsiElement;
import com.goide.stubs.GoReceiverStub;

public interface GoReceiver extends GoNamedElement, StubBasedPsiElement<GoReceiverStub> {

  @NotNull
  GoType getType();

  @Nullable
  PsiElement getIdentifier();

  @Nullable
  GoType getGoType();

}
