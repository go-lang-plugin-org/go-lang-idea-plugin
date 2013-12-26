// This is a generated file. Not intended for manual editing.
package com.goide.psi.impl;

import com.goide.psi.GoTypeDecl;
import com.goide.psi.GoTypeSpec;
import com.goide.psi.GoVisitor;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.goide.GoTypes.TYPE;

public class GoTypeDeclImpl extends GoCompositeElementImpl implements GoTypeDecl {

  public GoTypeDeclImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof GoVisitor) ((GoVisitor)visitor).visitTypeDecl(this);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<GoTypeSpec> getTypeSpecList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, GoTypeSpec.class);
  }

  @Override
  @NotNull
  public PsiElement getType() {
    return findNotNullChildByType(TYPE);
  }

}
