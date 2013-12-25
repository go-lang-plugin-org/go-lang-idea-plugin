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

public class GoStatementImpl extends GoCompositeElementImpl implements GoStatement {

  public GoStatementImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof GoVisitor) ((GoVisitor)visitor).visitStatement(this);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public GoBlock getBlock() {
    return findChildByClass(GoBlock.class);
  }

  @Override
  @Nullable
  public GoBreakStatement getBreakStatement() {
    return findChildByClass(GoBreakStatement.class);
  }

  @Override
  @Nullable
  public GoContinueStatement getContinueStatement() {
    return findChildByClass(GoContinueStatement.class);
  }

  @Override
  @Nullable
  public GoDeclarationStatement getDeclarationStatement() {
    return findChildByClass(GoDeclarationStatement.class);
  }

  @Override
  @Nullable
  public GoDeferStatement getDeferStatement() {
    return findChildByClass(GoDeferStatement.class);
  }

  @Override
  @Nullable
  public GoFallthroughStatement getFallthroughStatement() {
    return findChildByClass(GoFallthroughStatement.class);
  }

  @Override
  @Nullable
  public GoForStatement getForStatement() {
    return findChildByClass(GoForStatement.class);
  }

  @Override
  @Nullable
  public GoGoStatement getGoStatement() {
    return findChildByClass(GoGoStatement.class);
  }

  @Override
  @Nullable
  public GoGotoStatement getGotoStatement() {
    return findChildByClass(GoGotoStatement.class);
  }

  @Override
  @Nullable
  public GoIfStatement getIfStatement() {
    return findChildByClass(GoIfStatement.class);
  }

  @Override
  @Nullable
  public GoLabeledStatement getLabeledStatement() {
    return findChildByClass(GoLabeledStatement.class);
  }

  @Override
  @Nullable
  public GoReturnStatement getReturnStatement() {
    return findChildByClass(GoReturnStatement.class);
  }

  @Override
  @Nullable
  public GoSelectStatement getSelectStatement() {
    return findChildByClass(GoSelectStatement.class);
  }

  @Override
  @Nullable
  public GoSimpleStatement getSimpleStatement() {
    return findChildByClass(GoSimpleStatement.class);
  }

  @Override
  @Nullable
  public GoSwitchStatement getSwitchStatement() {
    return findChildByClass(GoSwitchStatement.class);
  }

}
