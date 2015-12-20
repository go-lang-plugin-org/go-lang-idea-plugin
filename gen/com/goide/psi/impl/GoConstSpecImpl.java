// This is a generated file. Not intended for manual editing.
package com.goide.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static com.goide.GoTypes.*;
import com.goide.stubs.GoConstSpecStub;
import com.goide.psi.*;
import com.intellij.psi.stubs.IStubElementType;

public class GoConstSpecImpl extends GoStubbedElementImpl<GoConstSpecStub> implements GoConstSpec {

  public GoConstSpecImpl(ASTNode node) {
    super(node);
  }

  public GoConstSpecImpl(GoConstSpecStub stub, IStubElementType nodeType) {
    super(stub, nodeType);
  }

  public void accept(@NotNull GoVisitor visitor) {
    visitor.visitConstSpec(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof GoVisitor) accept((GoVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<GoConstDefinition> getConstDefinitionList() {
    return findChildrenByClass(GoConstDefinition.class, com.goide.stubs.GoConstDefinitionStub.class);
  }

  @Override
  @NotNull
  public List<GoExpression> getExpressionList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, GoExpression.class);
  }

  @Override
  @Nullable
  public GoType getType() {
    return findChildByClass(GoType.class, com.goide.stubs.GoTypeStub.class);
  }

  @Override
  @Nullable
  public PsiElement getAssign() {
    return findChildByType(ASSIGN);
  }

  public void deleteDefinition(GoConstDefinition definitionToDelete) {
    GoPsiImplUtil.deleteDefinition(this, definitionToDelete);
  }

}
