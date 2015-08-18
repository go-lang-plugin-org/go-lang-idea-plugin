// This is a generated file. Not intended for manual editing.
package com.goide.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static com.goide.GoTypes.*;
import com.goide.stubs.GoTypeSpecStub;
import com.goide.psi.*;
import com.intellij.psi.ResolveState;
import com.intellij.psi.stubs.IStubElementType;

public class GoTypeSpecImpl extends GoNamedElementImpl<GoTypeSpecStub> implements GoTypeSpec {

  public GoTypeSpecImpl(ASTNode node) {
    super(node);
  }

  public GoTypeSpecImpl(GoTypeSpecStub stub, IStubElementType nodeType) {
    super(stub, nodeType);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof GoVisitor) ((GoVisitor)visitor).visitTypeSpec(this);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public GoSpecType getSpecType() {
    return findNotNullChildByClass(GoSpecType.class);
  }

  @Nullable
  public GoType getGoTypeInner(ResolveState context) {
    return GoPsiImplUtil.getGoTypeInner(this, context);
  }

  @NotNull
  public List<GoMethodDeclaration> getMethods() {
    return GoPsiImplUtil.getMethods(this);
  }

  public boolean shouldGoDeeper() {
    return GoPsiImplUtil.shouldGoDeeper(this);
  }

  @Override
  @NotNull
  public PsiElement getIdentifier() {
    GoSpecType p1 = getSpecType();
    return p1.getIdentifier();
  }

}
