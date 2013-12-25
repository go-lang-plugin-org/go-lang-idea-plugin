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

public class GoExpressionImpl extends GoCompositeElementImpl implements GoExpression {

  public GoExpressionImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof GoVisitor) ((GoVisitor)visitor).visitExpression(this);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public GoExpression getExpression() {
    return findChildByClass(GoExpression.class);
  }

  @Override
  @NotNull
  public GoUnaryExpr getUnaryExpr() {
    return findNotNullChildByClass(GoUnaryExpr.class);
  }

  @Override
  @Nullable
  public GoBinaryOp getBinaryOp() {
    return findChildByClass(GoBinaryOp.class);
  }

}
