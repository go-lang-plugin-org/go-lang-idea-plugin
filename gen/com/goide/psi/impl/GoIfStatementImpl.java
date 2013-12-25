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

public class GoIfStatementImpl extends GoCompositeElementImpl implements GoIfStatement {

  public GoIfStatementImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof GoVisitor) ((GoVisitor)visitor).visitIfStatement(this);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<GoBlock> getBlockList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, GoBlock.class);
  }

  @Override
  @NotNull
  public GoExpression getExpression() {
    return findNotNullChildByClass(GoExpression.class);
  }

  @Override
  @Nullable
  public GoIfStatement getIfStatement() {
    return findChildByClass(GoIfStatement.class);
  }

  @Override
  @Nullable
  public GoSimpleStatement getSimpleStatement() {
    return findChildByClass(GoSimpleStatement.class);
  }

  @Override
  @Nullable
  public PsiElement getElse() {
    return findChildByType(GO_ELSE);
  }

  @Override
  @NotNull
  public PsiElement getIf() {
    return findNotNullChildByType(GO_IF);
  }

}
