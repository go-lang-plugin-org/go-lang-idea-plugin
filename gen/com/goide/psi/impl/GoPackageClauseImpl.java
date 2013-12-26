// This is a generated file. Not intended for manual editing.
package com.goide.psi.impl;

import com.goide.psi.GoPackageClause;
import com.goide.psi.GoVisitor;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.goide.GoTypes.IDENTIFIER;
import static com.goide.GoTypes.PACKAGE;

public class GoPackageClauseImpl extends GoCompositeElementImpl implements GoPackageClause {

  public GoPackageClauseImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof GoVisitor) ((GoVisitor)visitor).visitPackageClause(this);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public PsiElement getIdentifier() {
    return findChildByType(IDENTIFIER);
  }

  @Override
  @NotNull
  public PsiElement getPackage() {
    return findNotNullChildByType(PACKAGE);
  }

}
