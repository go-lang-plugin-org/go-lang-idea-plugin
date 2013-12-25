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

public class GoSwitchStatementImpl extends GoCompositeElementImpl implements GoSwitchStatement {

  public GoSwitchStatementImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof GoVisitor) ((GoVisitor)visitor).visitSwitchStatement(this);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public GoExprSwitchStatement getExprSwitchStatement() {
    return findChildByClass(GoExprSwitchStatement.class);
  }

  @Override
  @Nullable
  public GoTypeSwitchStatement getTypeSwitchStatement() {
    return findChildByClass(GoTypeSwitchStatement.class);
  }

}
