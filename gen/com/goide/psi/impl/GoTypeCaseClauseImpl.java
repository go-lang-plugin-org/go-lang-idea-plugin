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

public class GoTypeCaseClauseImpl extends GoCompositeElementImpl implements GoTypeCaseClause {

  public GoTypeCaseClauseImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof GoVisitor) ((GoVisitor)visitor).visitTypeCaseClause(this);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public GoStatementList getStatementList() {
    return findNotNullChildByClass(GoStatementList.class);
  }

  @Override
  @NotNull
  public GoTypeSwitchCase getTypeSwitchCase() {
    return findNotNullChildByClass(GoTypeSwitchCase.class);
  }

}
