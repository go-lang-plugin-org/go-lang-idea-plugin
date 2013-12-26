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

public class GoExprSwitchStatementImpl extends GoCompositeElementImpl implements GoExprSwitchStatement {

  public GoExprSwitchStatementImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof GoVisitor) ((GoVisitor)visitor).visitExprSwitchStatement(this);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<GoExprCaseClause> getExprCaseClauseList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, GoExprCaseClause.class);
  }

  @Override
  @Nullable
  public GoExpression getExpression() {
    return findChildByClass(GoExpression.class);
  }

  @Override
  @Nullable
  public GoSimpleStatement getSimpleStatement() {
    return findChildByClass(GoSimpleStatement.class);
  }

  @Override
  @NotNull
  public PsiElement getSwitch() {
    return findNotNullChildByType(SWITCH);
  }

}
