// This is a generated file. Not intended for manual editing.
package com.goide.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.StubBasedPsiElement;
import com.goide.stubs.GoMethodSpecStub;

public interface GoMethodSpec extends GoReceiverHolder, StubBasedPsiElement<GoMethodSpecStub> {

  @Nullable
  GoSignature getSignature();

  @Nullable
  GoTypeReferenceExpression getTypeReferenceExpression();

  @Nullable
  PsiElement getIdentifier();

  @Nullable
  GoType getGoType();

  @Nullable
  String getName();

}
