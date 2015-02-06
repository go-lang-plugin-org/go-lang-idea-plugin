// This is a generated file. Not intended for manual editing.
package com.goide.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.StubBasedPsiElement;
import com.goide.stubs.GoImportSpecStub;

public interface GoImportSpec extends GoNamedElement, StubBasedPsiElement<GoImportSpecStub> {

  @NotNull
  GoImportString getImportString();

  @Nullable
  PsiElement getDot();

  @Nullable
  PsiElement getIdentifier();

  String getAlias();

  String getLocalPackageName();

}
