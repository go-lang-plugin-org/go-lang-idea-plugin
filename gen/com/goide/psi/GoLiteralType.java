// This is a generated file. Not intended for manual editing.
package com.goide.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface GoLiteralType extends GoCompositeElement {

  @Nullable
  GoArrayType getArrayType();

  @Nullable
  GoElementType getElementType();

  @Nullable
  GoMapType getMapType();

  @Nullable
  GoSliceType getSliceType();

  @Nullable
  GoStructType getStructType();

  @Nullable
  GoTypeName getTypeName();

}
