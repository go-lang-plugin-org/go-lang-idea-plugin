// This is a generated file. Not intended for manual editing.
package com.goide.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface GoChannelType extends GoType {

  @NotNull
  GoType getType();

  @Nullable
  PsiElement getSendChannel();

  @NotNull
  PsiElement getChan();

}
