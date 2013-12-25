// This is a generated file. Not intended for manual editing.
package com.goide.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static com.goide.GoTypes.*;
import com.goide.psi.*;

public class GoPrimaryExprImpl extends GoCompositeElementImpl implements GoPrimaryExpr {

  public GoPrimaryExprImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof GoVisitor) ((GoVisitor)visitor).visitPrimaryExpr(this);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public GoBuiltinCall getBuiltinCall() {
    return findChildByClass(GoBuiltinCall.class);
  }

  @Override
  @Nullable
  public GoCall getCall() {
    return findChildByClass(GoCall.class);
  }

  @Override
  @Nullable
  public GoConversion getConversion() {
    return findChildByClass(GoConversion.class);
  }

  @Override
  @Nullable
  public GoIndex getIndex() {
    return findChildByClass(GoIndex.class);
  }

  @Override
  @Nullable
  public GoOperand getOperand() {
    return findChildByClass(GoOperand.class);
  }

  @Override
  @Nullable
  public GoPrimaryExpr getPrimaryExpr() {
    return findChildByClass(GoPrimaryExpr.class);
  }

  @Override
  @Nullable
  public GoSelector getSelector() {
    return findChildByClass(GoSelector.class);
  }

  @Override
  @Nullable
  public GoSlice getSlice() {
    return findChildByClass(GoSlice.class);
  }

  @Override
  @Nullable
  public GoTypeAssertion getTypeAssertion() {
    return findChildByClass(GoTypeAssertion.class);
  }

}
