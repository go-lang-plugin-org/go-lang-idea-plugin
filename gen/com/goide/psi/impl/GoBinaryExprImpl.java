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

public class GoBinaryExprImpl extends GoExpressionImpl implements GoBinaryExpr {

  public GoBinaryExprImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof GoVisitor) ((GoVisitor)visitor).visitBinaryExpr(this);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<GoExpression> getExpressionList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, GoExpression.class);
  }

  @Override
  @NotNull
  public GoExpression getLeft() {
    List<GoExpression> p1 = getExpressionList();
    return p1.get(0);
  }

  @Override
  @Nullable
  public GoExpression getRight() {
    List<GoExpression> p1 = getExpressionList();
    return p1.size() < 2 ? null : p1.get(1);
  }

}
