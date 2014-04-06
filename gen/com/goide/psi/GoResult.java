// This is a generated file. Not intended for manual editing.
package com.goide.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.StubBasedPsiElement;
import com.goide.stubs.GoResultStub;

public interface GoResult extends GoCompositeElement, StubBasedPsiElement<GoResultStub> {

  @Nullable
  GoParameters getParameters();

  @Nullable
  GoType getType();

  @Nullable
  PsiElement getLparen();

  @Nullable
  PsiElement getRparen();

}
