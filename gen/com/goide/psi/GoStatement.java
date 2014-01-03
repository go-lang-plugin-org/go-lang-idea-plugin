// This is a generated file. Not intended for manual editing.
package com.goide.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface GoStatement extends GoCompositeElement {

  @Nullable
  GoBlock getBlock();

  @Nullable
  GoConstDeclaration getConstDeclaration();

  @Nullable
  GoTypeDeclaration getTypeDeclaration();

  @Nullable
  GoVarDeclaration getVarDeclaration();

}
