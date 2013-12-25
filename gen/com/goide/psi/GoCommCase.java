// This is a generated file. Not intended for manual editing.
package com.goide.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface GoCommCase extends GoCompositeElement {

  @Nullable
  GoRecvStatement getRecvStatement();

  @Nullable
  GoSendStatement getSendStatement();

  @Nullable
  PsiElement getCase();

  @Nullable
  PsiElement getDefault();

}
