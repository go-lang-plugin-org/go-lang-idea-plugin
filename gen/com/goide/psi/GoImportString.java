// This is a generated file. Not intended for manual editing.
package com.goide.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiReference;

public interface GoImportString extends GoCompositeElement {

  @Nullable
  PsiElement getRawString();

  @Nullable
  PsiElement getString();

  @NotNull
  PsiReference[] getReferences();

  @Nullable
  PsiDirectory resolve();

  @NotNull
  String getPath();

  @NotNull
  TextRange getPathTextRange();

}
