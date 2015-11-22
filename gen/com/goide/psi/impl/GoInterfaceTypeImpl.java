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

public class GoInterfaceTypeImpl extends GoTypeImpl implements GoInterfaceType {

  public GoInterfaceTypeImpl(ASTNode node) {
    super(node);
  }

  public GoInterfaceTypeImpl(com.goide.stubs.GoTypeStub stub, IStubElementType nodeType) {
    super(stub, nodeType);
  }

  public void accept(@NotNull GoVisitor visitor) {
    visitor.visitInterfaceType(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof GoVisitor) accept((GoVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<GoMethodSpec> getMethodSpecList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, GoMethodSpec.class);
  }

  @Override
  @Nullable
  public PsiElement getLbrace() {
    return findChildByType(LBRACE);
  }

  @Override
  @Nullable
  public PsiElement getRbrace() {
    return findChildByType(RBRACE);
  }

  @Override
  @NotNull
  public PsiElement getInterface() {
    return findNotNullChildByType(INTERFACE);
  }

  @NotNull
  public List<GoMethodSpec> getMethods() {
    return GoPsiImplUtil.getMethods(this);
  }

  @NotNull
  public List<GoTypeReferenceExpression> getBaseTypesReferences() {
    return GoPsiImplUtil.getBaseTypesReferences(this);
  }

}
