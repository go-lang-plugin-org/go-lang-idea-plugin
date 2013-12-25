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

public class GoVarDeclImpl extends GoCompositeElementImpl implements GoVarDecl {

  public GoVarDeclImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof GoVisitor) ((GoVisitor)visitor).visitVarDecl(this);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<GoVarSpec> getVarSpecList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, GoVarSpec.class);
  }

  @Override
  @NotNull
  public PsiElement getVar() {
    return findNotNullChildByType(GO_VAR);
  }

}
