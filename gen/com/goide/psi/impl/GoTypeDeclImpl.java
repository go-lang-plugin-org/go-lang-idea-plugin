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

}
