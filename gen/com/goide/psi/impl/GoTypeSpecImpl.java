// This is a generated file. Not intended for manual editing.
package com.goide.psi.impl;

import com.goide.psi.GoGoType;
import com.goide.psi.GoTypeSpec;
import com.goide.psi.GoVisitor;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import org.jetbrains.annotations.NotNull;

import static com.goide.GoTypes.IDENTIFIER;

public class GoTypeSpecImpl extends GoCompositeElementImpl implements GoTypeSpec {

  public GoTypeSpecImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof GoVisitor) ((GoVisitor)visitor).visitTypeSpec(this);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public GoGoType getGoType() {
    return findNotNullChildByClass(GoGoType.class);
  }

  @Override
  @NotNull
  public PsiElement getIdentifier() {
    return findNotNullChildByType(IDENTIFIER);
  }

}
