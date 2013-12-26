// This is a generated file. Not intended for manual editing.
package com.goide.psi.impl;

import com.goide.psi.GoQualifiedIdent;
import com.goide.psi.GoTypeName;
import com.goide.psi.GoVisitor;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.goide.GoTypes.IDENTIFIER;

public class GoTypeNameImpl extends GoCompositeElementImpl implements GoTypeName {

  public GoTypeNameImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof GoVisitor) ((GoVisitor)visitor).visitTypeName(this);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public GoQualifiedIdent getQualifiedIdent() {
    return findChildByClass(GoQualifiedIdent.class);
  }

  @Override
  @Nullable
  public PsiElement getIdentifier() {
    return findChildByType(IDENTIFIER);
  }

}
