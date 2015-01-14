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

public class GoMapTypeImpl extends GoTypeImpl implements GoMapType {

  public GoMapTypeImpl(ASTNode node) {
    super(node);
  }

  public GoMapTypeImpl(com.goide.stubs.GoTypeStub stub, IStubElementType nodeType) {
    super(stub, nodeType);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof GoVisitor) ((GoVisitor)visitor).visitMapType(this);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<GoType> getTypeList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, GoType.class);
  }

  @Override
  @Nullable
  public PsiElement getLbrack() {
    return findChildByType(LBRACK);
  }

  @Override
  @Nullable
  public PsiElement getRbrack() {
    return findChildByType(RBRACK);
  }

  @Override
  @NotNull
  public PsiElement getMap() {
    return findNotNullChildByType(MAP);
  }

  @Override
  @Nullable
  public GoType getKeyType() {
    List<GoType> p1 = getTypeList();
    return p1.size() < 1 ? null : p1.get(0);
  }

  @Override
  @Nullable
  public GoType getValueType() {
    List<GoType> p1 = getTypeList();
    return p1.size() < 2 ? null : p1.get(1);
  }

}
