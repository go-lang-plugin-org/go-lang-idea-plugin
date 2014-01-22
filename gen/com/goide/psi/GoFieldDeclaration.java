// This is a generated file. Not intended for manual editing.
package com.goide.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface GoFieldDeclaration extends GoCompositeElement {

  @Nullable
  GoAnonymousFieldDefinition getAnonymousFieldDefinition();

  @NotNull
  List<GoFieldDefinition> getFieldDefinitionList();

  @Nullable
  GoTag getTag();

  @Nullable
  GoType getType();

}
