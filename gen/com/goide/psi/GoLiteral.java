// This is a generated file. Not intended for manual editing.
package com.goide.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface GoLiteral extends GoCompositeElement {

  @Nullable
  GoBasicLit getBasicLit();

  @Nullable
  GoCompositeLit getCompositeLit();

  @Nullable
  GoFunctionLit getFunctionLit();

}
