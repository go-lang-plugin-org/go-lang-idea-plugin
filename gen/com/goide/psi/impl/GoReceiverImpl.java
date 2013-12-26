// This is a generated file. Not intended for manual editing.
package com.goide.psi.impl;

import com.goide.psi.GoBaseTypeName;
import com.goide.psi.GoReceiver;
import com.goide.psi.GoVisitor;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.goide.GoTypes.IDENTIFIER;

public class GoReceiverImpl extends GoCompositeElementImpl implements GoReceiver {

  public GoReceiverImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof GoVisitor) ((GoVisitor)visitor).visitReceiver(this);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public GoBaseTypeName getBaseTypeName() {
    return findNotNullChildByClass(GoBaseTypeName.class);
  }

  @Override
  @Nullable
  public PsiElement getIdentifier() {
    return findChildByType(IDENTIFIER);
  }

}
