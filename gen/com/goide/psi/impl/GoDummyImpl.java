// This is a generated file. Not intended for manual editing.
package com.goide.psi.impl;

import com.goide.psi.GoDummy;
import com.goide.psi.GoVisitor;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.goide.GoTypes.*;

public class GoDummyImpl extends GoCompositeElementImpl implements GoDummy {

  public GoDummyImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof GoVisitor) ((GoVisitor)visitor).visitDummy(this);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public PsiElement getChar() {
    return findChildByType(CHAR);
  }

  @Override
  @Nullable
  public PsiElement getHex() {
    return findChildByType(HEX);
  }

  @Override
  @Nullable
  public PsiElement getOct() {
    return findChildByType(OCT);
  }

}
