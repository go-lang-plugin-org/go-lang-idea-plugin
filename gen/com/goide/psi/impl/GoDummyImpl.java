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
    return findChildByType(GO_CHAR);
  }

  @Override
  @Nullable
  public PsiElement getHex() {
    return findChildByType(GO_HEX);
  }

  @Override
  @Nullable
  public PsiElement getOct() {
    return findChildByType(GO_OCT);
  }

}
