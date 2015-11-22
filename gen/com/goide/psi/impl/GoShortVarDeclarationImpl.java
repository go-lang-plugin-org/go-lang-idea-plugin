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
import com.intellij.psi.stubs.IStubElementType;

public class GoShortVarDeclarationImpl extends GoVarSpecImpl implements GoShortVarDeclaration {

  public GoShortVarDeclarationImpl(ASTNode node) {
    super(node);
  }

  public GoShortVarDeclarationImpl(com.goide.stubs.GoVarSpecStub stub, IStubElementType nodeType) {
    super(stub, nodeType);
  }

  public void accept(@NotNull GoVisitor visitor) {
    visitor.visitShortVarDeclaration(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof GoVisitor) accept((GoVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<GoExpression> getExpressionList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, GoExpression.class);
  }

  @Override
  @NotNull
  public List<GoVarDefinition> getVarDefinitionList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, GoVarDefinition.class);
  }

  @Override
  @NotNull
  public PsiElement getVarAssign() {
    return findNotNullChildByType(VAR_ASSIGN);
  }

}
