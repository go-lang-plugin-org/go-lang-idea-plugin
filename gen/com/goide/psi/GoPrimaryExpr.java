// This is a generated file. Not intended for manual editing.
package com.goide.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface GoPrimaryExpr extends GoCompositeElement {

  @Nullable
  GoBuiltinCall getBuiltinCall();

  @Nullable
  GoCall getCall();

  @Nullable
  GoConversion getConversion();

  @Nullable
  GoIndex getIndex();

  @Nullable
  GoOperand getOperand();

  @Nullable
  GoPrimaryExpr getPrimaryExpr();

  @Nullable
  GoSelector getSelector();

  @Nullable
  GoSlice getSlice();

  @Nullable
  GoTypeAssertion getTypeAssertion();

}
