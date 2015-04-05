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

public class GoSimpleStatementImpl extends GoStatementImpl implements GoSimpleStatement {

  public GoSimpleStatementImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof GoVisitor) ((GoVisitor)visitor).visitSimpleStatement(this);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public GoLeftHandExprList getLeftHandExprList() {
    return findChildByClass(GoLeftHandExprList.class);
  }

  @Override
  @Nullable
  public GoShortVarDeclaration getShortVarDeclaration() {
    return findChildByClass(GoShortVarDeclaration.class);
  }

  @Override
  @Nullable
  public GoStatement getStatement() {
    return findChildByClass(GoStatement.class);
  }

  @Override
  @Nullable
  public PsiElement getMinusMinus() {
    return findChildByType(MINUS_MINUS);
  }

  @Override
  @Nullable
  public PsiElement getPlusPlus() {
    return findChildByType(PLUS_PLUS);
  }

}
