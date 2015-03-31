// This is a generated file. Not intended for manual editing.
package com.goide.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.StubBasedPsiElement;
import com.goide.stubs.GoReceiverStub;
import com.intellij.psi.ResolveState;

public interface GoReceiver extends GoNamedElement, StubBasedPsiElement<GoReceiverStub> {

  @Nullable
  GoType getType();

  @Nullable
  PsiElement getComma();

  @NotNull
  PsiElement getLparen();

  @Nullable
  PsiElement getMul();

  @Nullable
  PsiElement getRparen();

  @Nullable
  PsiElement getIdentifier();

  @Nullable
  GoType getGoType(ResolveState context);

}
