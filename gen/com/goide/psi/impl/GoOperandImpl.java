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

public class GoOperandImpl extends GoCompositeElementImpl implements GoOperand {

  public GoOperandImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof GoVisitor) ((GoVisitor)visitor).visitOperand(this);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public GoExpression getExpression() {
    return findChildByClass(GoExpression.class);
  }

  @Override
  @Nullable
  public GoLiteral getLiteral() {
    return findChildByClass(GoLiteral.class);
  }

  @Override
  @Nullable
  public GoMethodExpr getMethodExpr() {
    return findChildByClass(GoMethodExpr.class);
  }

  @Override
  @Nullable
  public GoOperandName getOperandName() {
    return findChildByClass(GoOperandName.class);
  }

}
