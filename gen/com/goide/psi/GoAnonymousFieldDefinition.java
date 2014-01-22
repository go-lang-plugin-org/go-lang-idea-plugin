// This is a generated file. Not intended for manual editing.
package com.goide.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.StubBasedPsiElement;
import com.goide.stubs.GoAnonymousFieldDefinitionStub;

public interface GoAnonymousFieldDefinition extends GoNamedElement, StubBasedPsiElement<GoAnonymousFieldDefinitionStub> {

  @NotNull
  GoTypeReferenceExpression getTypeReferenceExpression();

  @Nullable
  PsiElement getMul();

  @Nullable
  PsiElement getIdentifier();

  @NotNull
  String getName();

  int getTextOffset();

  @Nullable
  GoType getGoType();

}
