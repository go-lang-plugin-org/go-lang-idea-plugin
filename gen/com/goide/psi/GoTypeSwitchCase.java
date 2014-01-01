// This is a generated file. Not intended for manual editing.
package com.goide.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface GoTypeSwitchCase extends GoCompositeElement {

  @NotNull
  List<GoType> getTypeList();

  @Nullable
  PsiElement getCase();

  @Nullable
  PsiElement getDefault();

}
