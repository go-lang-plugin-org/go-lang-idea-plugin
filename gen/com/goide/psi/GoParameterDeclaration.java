// This is a generated file. Not intended for manual editing.
package com.goide.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface GoParameterDeclaration extends GoCompositeElement {

  @NotNull
  List<GoParamDefinition> getParamDefinitionList();

  @NotNull
  GoType getType();

  @Nullable
  PsiElement getTripleDot();

}
