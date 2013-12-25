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

public class GoVarSpecImpl extends GoCompositeElementImpl implements GoVarSpec {

  public GoVarSpecImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof GoVisitor) ((GoVisitor)visitor).visitVarSpec(this);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public GoExpressionList getExpressionList() {
    return findChildByClass(GoExpressionList.class);
  }

  @Override
  @Nullable
  public GoGoType getGoType() {
    return findChildByClass(GoGoType.class);
  }

  @Override
  @NotNull
  public GoIdentifierList getIdentifierList() {
    return findNotNullChildByClass(GoIdentifierList.class);
  }

}
