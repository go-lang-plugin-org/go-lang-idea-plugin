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

public class GoSwitchStatementImpl extends GoStatementImpl implements GoSwitchStatement {

  public GoSwitchStatementImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull GoVisitor visitor) {
    visitor.visitSwitchStatement(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof GoVisitor) accept((GoVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public GoSwitchStart getSwitchStart() {
    return findChildByClass(GoSwitchStart.class);
  }

  @Override
  @Nullable
  public GoSwitchStatement getSwitchStatement() {
    return findChildByClass(GoSwitchStatement.class);
  }

}
