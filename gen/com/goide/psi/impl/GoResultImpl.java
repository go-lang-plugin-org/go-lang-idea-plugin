// This is a generated file. Not intended for manual editing.
package com.goide.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static com.goide.GoTypes.*;
import com.goide.stubs.GoResultStub;
import com.goide.psi.*;
import com.intellij.psi.stubs.IStubElementType;

public class GoResultImpl extends GoStubbedElementImpl<GoResultStub> implements GoResult {

  public GoResultImpl(ASTNode node) {
    super(node);
  }

  public GoResultImpl(GoResultStub stub, IStubElementType nodeType) {
    super(stub, nodeType);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof GoVisitor) ((GoVisitor)visitor).visitResult(this);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public GoParameters getParameters() {
    return findChildByClass(GoParameters.class, com.goide.stubs.GoParametersStub.class);
  }

  @Override
  @Nullable
  public GoType getType() {
    return findChildByClass(GoType.class, com.goide.stubs.GoTypeStub.class);
  }

  @Override
  @Nullable
  public PsiElement getLparen() {
    return findChildByType(LPAREN);
  }

  @Override
  @Nullable
  public PsiElement getRparen() {
    return findChildByType(RPAREN);
  }

}
