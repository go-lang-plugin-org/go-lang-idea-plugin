// This is a generated file. Not intended for manual editing.
package com.goide.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static com.goide.GoTypes.*;
import com.goide.stubs.GoAnonymousFieldDefinitionStub;
import com.goide.psi.*;
import com.intellij.psi.ResolveState;
import com.intellij.psi.stubs.IStubElementType;

public class GoAnonymousFieldDefinitionImpl extends GoNamedElementImpl<GoAnonymousFieldDefinitionStub> implements GoAnonymousFieldDefinition {

  public GoAnonymousFieldDefinitionImpl(ASTNode node) {
    super(node);
  }

  public GoAnonymousFieldDefinitionImpl(GoAnonymousFieldDefinitionStub stub, IStubElementType nodeType) {
    super(stub, nodeType);
  }

  public void accept(@NotNull GoVisitor visitor) {
    visitor.visitAnonymousFieldDefinition(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof GoVisitor) accept((GoVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public GoTypeReferenceExpression getTypeReferenceExpression() {
    return findNotNullChildByClass(GoTypeReferenceExpression.class);
  }

  @Override
  @Nullable
  public PsiElement getMul() {
    return findChildByType(MUL);
  }

  @Nullable
  public PsiElement getIdentifier() {
    return GoPsiImplUtil.getIdentifier(this);
  }

  @NotNull
  public String getName() {
    return GoPsiImplUtil.getName(this);
  }

  public int getTextOffset() {
    return GoPsiImplUtil.getTextOffset(this);
  }

  @Nullable
  public GoType getGoTypeInner(ResolveState context) {
    return GoPsiImplUtil.getGoTypeInner(this, context);
  }

}
