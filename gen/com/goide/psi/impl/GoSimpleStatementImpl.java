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

public class GoSimpleStatementImpl extends GoCompositeElementImpl implements GoSimpleStatement {

  public GoSimpleStatementImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof GoVisitor) ((GoVisitor)visitor).visitSimpleStatement(this);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public GoAssignment getAssignment() {
    return findChildByClass(GoAssignment.class);
  }

  @Override
  @Nullable
  public GoExpressionStatement getExpressionStatement() {
    return findChildByClass(GoExpressionStatement.class);
  }

  @Override
  @Nullable
  public GoIncDecStatement getIncDecStatement() {
    return findChildByClass(GoIncDecStatement.class);
  }

  @Override
  @Nullable
  public GoSendStatement getSendStatement() {
    return findChildByClass(GoSendStatement.class);
  }

  @Override
  @Nullable
  public GoShortVarDecl getShortVarDecl() {
    return findChildByClass(GoShortVarDecl.class);
  }

}
