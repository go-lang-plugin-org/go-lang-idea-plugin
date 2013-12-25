// This is a generated file. Not intended for manual editing.
package com.goide.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface GoTypeLit extends GoCompositeElement {

  @Nullable
  GoArrayType getArrayType();

  @Nullable
  GoChannelType getChannelType();

  @Nullable
  GoFunctionType getFunctionType();

  @Nullable
  GoInterfaceType getInterfaceType();

  @Nullable
  GoMapType getMapType();

  @Nullable
  GoPointerType getPointerType();

  @Nullable
  GoSliceType getSliceType();

  @Nullable
  GoStructType getStructType();

}
