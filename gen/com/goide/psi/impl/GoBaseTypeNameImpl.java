// This is a generated file. Not intended for manual editing.
package com.goide.psi.impl;

import com.goide.psi.GoBaseTypeName;
import com.goide.psi.GoVisitor;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import org.jetbrains.annotations.NotNull;

import static com.goide.GoTypes.IDENTIFIER;

public class GoBaseTypeNameImpl extends GoCompositeElementImpl implements GoBaseTypeName {

  public GoBaseTypeNameImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof GoVisitor) ((GoVisitor)visitor).visitBaseTypeName(this);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public PsiElement getIdentifier() {
    return findNotNullChildByType(IDENTIFIER);
  }

}
