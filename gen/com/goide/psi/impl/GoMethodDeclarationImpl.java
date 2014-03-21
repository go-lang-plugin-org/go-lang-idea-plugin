// This is a generated file. Not intended for manual editing.
package com.goide.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static com.goide.GoTypes.*;
import com.goide.stubs.GoMethodDeclarationStub;
import com.goide.psi.*;
import com.intellij.psi.stubs.IStubElementType;

public class GoMethodDeclarationImpl extends GoFunctionOrMethodDeclarationImpl<GoMethodDeclarationStub> implements GoMethodDeclaration {

  public GoMethodDeclarationImpl(ASTNode node) {
    super(node);
  }

  public GoMethodDeclarationImpl(GoMethodDeclarationStub stub, IStubElementType nodeType) {
    super(stub, nodeType);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof GoVisitor) ((GoVisitor)visitor).visitMethodDeclaration(this);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public GoBlock getBlock() {
    return findChildByClass(GoBlock.class);
  }

  @Override
  @NotNull
  public GoReceiver getReceiver() {
    return findNotNullChildByClass(GoReceiver.class);
  }

  @Override
  @Nullable
  public GoSignature getSignature() {
    return findChildByClass(GoSignature.class);
  }

  @Override
  @NotNull
  public PsiElement getFunc() {
    return findNotNullChildByType(FUNC);
  }

  @Override
  @Nullable
  public PsiElement getIdentifier() {
    return findChildByType(IDENTIFIER);
  }

}
