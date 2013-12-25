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

public class GoLiteralImpl extends GoCompositeElementImpl implements GoLiteral {

  public GoLiteralImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof GoVisitor) ((GoVisitor)visitor).visitLiteral(this);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public GoBasicLit getBasicLit() {
    return findChildByClass(GoBasicLit.class);
  }

  @Override
  @Nullable
  public GoCompositeLit getCompositeLit() {
    return findChildByClass(GoCompositeLit.class);
  }

  @Override
  @Nullable
  public GoFunctionLit getFunctionLit() {
    return findChildByClass(GoFunctionLit.class);
  }

}
