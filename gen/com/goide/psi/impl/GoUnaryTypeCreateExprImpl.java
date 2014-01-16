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

public class GoUnaryTypeCreateExprImpl extends GoUnaryExprImpl implements GoUnaryTypeCreateExpr {

  public GoUnaryTypeCreateExprImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof GoVisitor) ((GoVisitor)visitor).visitUnaryTypeCreateExpr(this);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public GoLiteralValue getLiteralValue() {
    return findNotNullChildByClass(GoLiteralValue.class);
  }

  @Override
  @NotNull
  public GoTypeReferenceExpression getTypeReferenceExpression() {
    return findNotNullChildByClass(GoTypeReferenceExpression.class);
  }

}
