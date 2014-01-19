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

public class GoParameterDeclarationImpl extends GoCompositeElementImpl implements GoParameterDeclaration {

  public GoParameterDeclarationImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof GoVisitor) ((GoVisitor)visitor).visitParameterDeclaration(this);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<GoParamDefinition> getParamDefinitionList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, GoParamDefinition.class);
  }

  @Override
  @NotNull
  public GoType getType() {
    return findNotNullChildByClass(GoType.class);
  }

  @Override
  @Nullable
  public PsiElement getTripleDot() {
    return findChildByType(TRIPLE_DOT);
  }

}
