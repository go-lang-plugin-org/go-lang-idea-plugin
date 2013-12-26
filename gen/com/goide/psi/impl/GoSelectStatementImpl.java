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

public class GoSelectStatementImpl extends GoStatementImpl implements GoSelectStatement {

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
