// This is a generated file. Not intended for manual editing.
package com.goide.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface GoResult extends GoCompositeElement {

  @Nullable
  GoParameters getParameters();

  @NotNull
  List<GoType> getTypeList();

  @Nullable
  PsiElement getLparen();

  @Nullable
  PsiElement getRparen();

}
