// This is a generated file. Not intended for manual editing.
package com.goide.psi.impl;

import com.goide.psi.GoInterfaceType;
import com.goide.psi.GoMethodSpec;
import com.goide.psi.GoVisitor;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.goide.GoTypes.INTERFACE;

public class GoInterfaceTypeImpl extends GoCompositeElementImpl implements GoInterfaceType {

  public GoInterfaceTypeImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof GoVisitor) ((GoVisitor)visitor).visitInterfaceType(this);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<GoMethodSpec> getMethodSpecList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, GoMethodSpec.class);
  }

  @Override
  @NotNull
  public PsiElement getInterface() {
    return findNotNullChildByType(INTERFACE);
  }

}
