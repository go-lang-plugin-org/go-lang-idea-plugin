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

public class GoMethodSpecImpl extends GoCompositeElementImpl implements GoMethodSpec {

  public GoMethodSpecImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof GoVisitor) ((GoVisitor)visitor).visitMethodSpec(this);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public GoInterfaceTypeName getInterfaceTypeName() {
    return findChildByClass(GoInterfaceTypeName.class);
  }

  @Override
  @Nullable
  public GoMethodName getMethodName() {
    return findChildByClass(GoMethodName.class);
  }

  @Override
  @Nullable
  public GoSignature getSignature() {
    return findChildByClass(GoSignature.class);
  }

}
