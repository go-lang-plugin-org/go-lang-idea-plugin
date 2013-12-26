// This is a generated file. Not intended for manual editing.
package com.goide.psi.impl;

import com.goide.psi.GoConstDecl;
import com.goide.psi.GoConstSpec;
import com.goide.psi.GoVisitor;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.goide.GoTypes.CONST;

public class GoConstDeclImpl extends GoCompositeElementImpl implements GoConstDecl {

  public GoConstDeclImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof GoVisitor) ((GoVisitor)visitor).visitConstDecl(this);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<GoConstSpec> getConstSpecList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, GoConstSpec.class);
  }

  @Override
  @NotNull
  public PsiElement getConst() {
    return findNotNullChildByType(CONST);
  }

}
