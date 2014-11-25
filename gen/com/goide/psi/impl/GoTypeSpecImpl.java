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
  @Nullable
  public GoType getType() {
    return findChildByClass(GoType.class);
  }

  @Override
  @NotNull
  public PsiElement getIdentifier() {
    return findNotNullChildByType(IDENTIFIER);
  }

  @Nullable
  public GoType getGoType() {
    return GoPsiImplUtil.getGoType(this);
  }

  @NotNull
  public List<GoMethodDeclaration> getMethods() {
    return GoPsiImplUtil.getMethods(this);
  }

}
