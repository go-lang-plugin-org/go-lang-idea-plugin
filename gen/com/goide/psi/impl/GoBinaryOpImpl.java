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

public class GoBinaryOpImpl extends GoCompositeElementImpl implements GoBinaryOp {

  public GoBinaryOpImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof GoVisitor) ((GoVisitor)visitor).visitBinaryOp(this);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public GoAddOp getAddOp() {
    return findChildByClass(GoAddOp.class);
  }

  @Override
  @Nullable
  public GoMulOp getMulOp() {
    return findChildByClass(GoMulOp.class);
  }

  @Override
  @Nullable
  public GoRelOp getRelOp() {
    return findChildByClass(GoRelOp.class);
  }

}
