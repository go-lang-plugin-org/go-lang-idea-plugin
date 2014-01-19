// This is a generated file. Not intended for manual editing.
package com.goide.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static com.goide.GoTypes.*;
import com.goide.stubs.GoReceiverStub;
import com.goide.psi.*;
import com.intellij.psi.stubs.IStubElementType;

public class GoReceiverImpl extends GoNamedElementImpl<GoReceiverStub> implements GoReceiver {

  public GoReceiverImpl(ASTNode node) {
    super(node);
  }

  public GoReceiverImpl(GoReceiverStub stub, IStubElementType nodeType) {
    super(stub, nodeType);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof GoVisitor) ((GoVisitor)visitor).visitReceiver(this);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public GoType getType() {
    return findNotNullChildByClass(GoType.class);
  }

  @Override
  @Nullable
  public PsiElement getIdentifier() {
    return findChildByType(IDENTIFIER);
  }

  @Nullable
  public GoType getGoType() {
    return GoPsiImplUtil.getGoType(this);
  }

}
