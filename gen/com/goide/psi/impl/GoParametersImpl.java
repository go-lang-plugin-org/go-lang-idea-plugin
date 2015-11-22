// This is a generated file. Not intended for manual editing.
package com.goide.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static com.goide.GoTypes.*;
import com.goide.stubs.GoParametersStub;
import com.goide.psi.*;
import com.intellij.psi.stubs.IStubElementType;

public class GoParametersImpl extends GoStubbedElementImpl<GoParametersStub> implements GoParameters {

  public GoParametersImpl(ASTNode node) {
    super(node);
  }

  public GoParametersImpl(GoParametersStub stub, IStubElementType nodeType) {
    super(stub, nodeType);
  }

  public void accept(@NotNull GoVisitor visitor) {
    visitor.visitParameters(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof GoVisitor) accept((GoVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<GoParameterDeclaration> getParameterDeclarationList() {
    return findChildrenByClass(GoParameterDeclaration.class, com.goide.stubs.GoParameterDeclarationStub.class);
  }

  @Override
  @Nullable
  public GoType getType() {
    return findChildByClass(GoType.class, com.goide.stubs.GoTypeStub.class);
  }

  @Override
  @NotNull
  public PsiElement getLparen() {
    return findNotNullChildByType(LPAREN);
  }

  @Override
  @Nullable
  public PsiElement getRparen() {
    return findChildByType(RPAREN);
  }

}
