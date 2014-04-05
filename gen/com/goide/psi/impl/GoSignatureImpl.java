// This is a generated file. Not intended for manual editing.
package com.goide.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static com.goide.GoTypes.*;
import com.goide.stubs.GoSignatureStub;
import com.goide.psi.*;
import com.intellij.psi.stubs.IStubElementType;

public class GoSignatureImpl extends GoStubbedElementImpl<GoSignatureStub> implements GoSignature {

  public GoSignatureImpl(ASTNode node) {
    super(node);
  }

  public GoSignatureImpl(GoSignatureStub stub, IStubElementType nodeType) {
    super(stub, nodeType);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof GoVisitor) ((GoVisitor)visitor).visitSignature(this);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public GoParameters getParameters() {
    return findNotNullChildByClass(GoParameters.class);
  }

  @Override
  @Nullable
  public GoResult getResult() {
    return findChildByClass(GoResult.class);
  }

}
