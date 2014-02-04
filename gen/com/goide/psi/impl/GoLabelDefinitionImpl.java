// This is a generated file. Not intended for manual editing.
package com.goide.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static com.goide.GoTypes.*;
import com.goide.stubs.GoLabelDefinitionStub;
import com.goide.psi.*;
import com.intellij.psi.stubs.IStubElementType;

public class GoLabelDefinitionImpl extends GoNamedElementImpl<GoLabelDefinitionStub> implements GoLabelDefinition {

  public GoLabelDefinitionImpl(ASTNode node) {
    super(node);
  }

  public GoLabelDefinitionImpl(GoLabelDefinitionStub stub, IStubElementType nodeType) {
    super(stub, nodeType);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof GoVisitor) ((GoVisitor)visitor).visitLabelDefinition(this);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public PsiElement getIdentifier() {
    return findNotNullChildByType(IDENTIFIER);
  }

}
