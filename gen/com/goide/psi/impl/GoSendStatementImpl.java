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

public class GoSendStatementImpl extends GoStatementImpl implements GoSendStatement {

  public GoSendStatementImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof GoVisitor) ((GoVisitor)visitor).visitSendStatement(this);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<GoExpression> getExpressionList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, GoExpression.class);
  }

  @Override
  @Nullable
  public GoLeftHandExprList getLeftHandExprList() {
    return findChildByClass(GoLeftHandExprList.class);
  }

  @Override
  @NotNull
  public PsiElement getSendChannel() {
    return findNotNullChildByType(SEND_CHANNEL);
  }

}
