// This is a generated file. Not intended for manual editing.
package com.goide.psi.impl;

import com.goide.psi.GoCommClause;
import com.goide.psi.GoSelectStatement;
import com.goide.psi.GoVisitor;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.goide.GoTypes.SELECT;

public class GoSelectStatementImpl extends GoCompositeElementImpl implements GoSelectStatement {

  public GoSelectStatementImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof GoVisitor) ((GoVisitor)visitor).visitSelectStatement(this);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<GoCommClause> getCommClauseList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, GoCommClause.class);
  }

  @Override
  @NotNull
  public PsiElement getSelect() {
    return findNotNullChildByType(SELECT);
  }

}
