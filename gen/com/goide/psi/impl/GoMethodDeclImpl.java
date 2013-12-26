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

public class GoMethodDeclImpl extends GoCompositeElementImpl implements GoMethodDecl {

  public GoMethodDeclImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof GoVisitor) ((GoVisitor)visitor).visitMethodDecl(this);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public GoFunction getFunction() {
    return findChildByClass(GoFunction.class);
  }

  @Override
  @NotNull
  public GoMethodName getMethodName() {
    return findNotNullChildByClass(GoMethodName.class);
  }

  @Override
  @NotNull
  public GoReceiver getReceiver() {
    return findNotNullChildByClass(GoReceiver.class);
  }

  @Override
  @Nullable
  public GoSignature getSignature() {
    return findChildByClass(GoSignature.class);
  }

  @Override
  @NotNull
  public PsiElement getFunc() {
    return findNotNullChildByType(FUNC);
  }

}
