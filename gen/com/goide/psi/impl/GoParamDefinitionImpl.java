// This is a generated file. Not intended for manual editing.
package com.goide.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static com.goide.GoTypes.*;
import com.goide.stubs.GoParamDefinitionStub;
import com.goide.psi.*;
import com.intellij.psi.stubs.IStubElementType;

public class GoParamDefinitionImpl extends GoNamedElementImpl<GoParamDefinitionStub> implements GoParamDefinition {

  public GoParamDefinitionImpl(ASTNode node) {
    super(node);
  }

  public GoParamDefinitionImpl(GoParamDefinitionStub stub, IStubElementType nodeType) {
    super(stub, nodeType);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof GoVisitor) ((GoVisitor)visitor).visitParamDefinition(this);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public PsiElement getIdentifier() {
    return findNotNullChildByType(IDENTIFIER);
  }

  @NotNull
  public boolean isVariadic() {
    return GoPsiImplUtil.isVariadic(this);
  }

}
