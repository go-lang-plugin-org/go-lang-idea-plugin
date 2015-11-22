// This is a generated file. Not intended for manual editing.
package com.goide.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static com.goide.GoTypes.*;
import com.goide.stubs.GoVarSpecStub;
import com.goide.psi.*;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.stubs.IStubElementType;

public class GoVarSpecImpl extends GoStubbedElementImpl<GoVarSpecStub> implements GoVarSpec {

  public GoVarSpecImpl(ASTNode node) {
    super(node);
  }

  public GoVarSpecImpl(GoVarSpecStub stub, IStubElementType nodeType) {
    super(stub, nodeType);
  }

  public void accept(@NotNull GoVisitor visitor) {
    visitor.visitVarSpec(this);
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
  @Nullable
  public GoType getType() {
    return findChildByClass(GoType.class, com.goide.stubs.GoTypeStub.class);
  }

  @Override
  @NotNull
  public List<GoVarDefinition> getVarDefinitionList() {
    return findChildrenByClass(GoVarDefinition.class, com.goide.stubs.GoVarDefinitionStub.class);
  }

  @Override
  @Nullable
  public PsiElement getAssign() {
    return findChildByType(ASSIGN);
  }

  public boolean processDeclarations(PsiScopeProcessor processor, ResolveState state, PsiElement lastParent, PsiElement place) {
    return GoPsiImplUtil.processDeclarations(this, processor, state, lastParent, place);
  }

}
