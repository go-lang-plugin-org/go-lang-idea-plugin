// This is a generated file. Not intended for manual editing.
package com.goide.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.StubBasedPsiElement;
import com.goide.stubs.GoMethodSpecStub;
import com.intellij.psi.ResolveState;

public interface GoMethodSpec extends GoNamedSignatureOwner, StubBasedPsiElement<GoMethodSpecStub> {

  @Nullable
  GoSignature getSignature();

  @Nullable
  GoTypeReferenceExpression getTypeReferenceExpression();

  @Nullable
  PsiElement getIdentifier();

  @Nullable
  GoType getGoTypeInner(ResolveState context);

  @Nullable
  String getName();

}
