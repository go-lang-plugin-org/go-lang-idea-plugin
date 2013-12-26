// This is a generated file. Not intended for manual editing.
package com.goide.psi.impl;

import com.goide.psi.GoFieldDecl;
import com.goide.psi.GoStructType;
import com.goide.psi.GoVisitor;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.goide.GoTypes.STRUCT;

public class GoStructTypeImpl extends GoCompositeElementImpl implements GoStructType {

  public GoStructTypeImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof GoVisitor) ((GoVisitor)visitor).visitStructType(this);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<GoFieldDecl> getFieldDeclList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, GoFieldDecl.class);
  }

  @Override
  @NotNull
  public PsiElement getStruct() {
    return findNotNullChildByType(STRUCT);
  }

}
