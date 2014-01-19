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

public class GoRangeClauseImpl extends GoCompositeElementImpl implements GoRangeClause {

  public GoRangeClauseImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof GoVisitor) ((GoVisitor)visitor).visitRangeClause(this);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<GoExpression> getExpressionList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, GoExpression.class);
  }

  @Override
  @NotNull
  public List<GoVarDefinition> getVarDefinitionList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, GoVarDefinition.class);
  }

  @Override
  @Nullable
  public PsiElement getAssign() {
    return findChildByType(ASSIGN);
  }

  @Override
  @Nullable
  public PsiElement getVarAssign() {
    return findChildByType(VAR_ASSIGN);
  }

  @Override
  @NotNull
  public PsiElement getRange() {
    return findNotNullChildByType(RANGE);
  }

}
