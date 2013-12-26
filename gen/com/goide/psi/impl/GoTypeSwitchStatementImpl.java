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

public class GoTypeSwitchStatementImpl extends GoCompositeElementImpl implements GoTypeSwitchStatement {

  public GoTypeSwitchStatementImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof GoVisitor) ((GoVisitor)visitor).visitTypeSwitchStatement(this);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public GoSimpleStatement getSimpleStatement() {
    return findChildByClass(GoSimpleStatement.class);
  }

  @Override
  @NotNull
  public List<GoTypeCaseClause> getTypeCaseClauseList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, GoTypeCaseClause.class);
  }

  @Override
  @NotNull
  public GoTypeSwitchGuard getTypeSwitchGuard() {
    return findNotNullChildByClass(GoTypeSwitchGuard.class);
  }

  @Override
  @NotNull
  public PsiElement getSwitch() {
    return findNotNullChildByType(SWITCH);
  }

}
