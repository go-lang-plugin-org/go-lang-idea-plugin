// This is a generated file. Not intended for manual editing.
package com.goide.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface GoMapType extends GoType {

  @NotNull
  List<GoType> getTypeList();

  @Nullable
  PsiElement getLbrack();

  @Nullable
  PsiElement getRbrack();

  @NotNull
  PsiElement getMap();

}
