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

public class GoConstDeclarationImpl extends GoCompositeElementImpl implements GoConstDeclaration {

  public GoConstDeclarationImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof GoVisitor) ((GoVisitor)visitor).visitConstDeclaration(this);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<GoConstSpec> getConstSpecList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, GoConstSpec.class);
  }

  @Override
  @NotNull
  public PsiElement getConst() {
    return findNotNullChildByType(CONST);
  }

}
