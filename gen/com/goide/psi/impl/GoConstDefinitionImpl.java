// This is a generated file. Not intended for manual editing.
package com.goide.psi.impl;

import com.goide.psi.GoConstDefinition;
import com.goide.psi.GoVisitor;
import com.goide.stubs.GoConstDefinitionStub;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.stubs.IStubElementType;
import org.jetbrains.annotations.NotNull;

import static com.goide.GoTypes.IDENTIFIER;

public class GoConstDefinitionImpl extends GoNamedElementImpl<GoConstDefinitionStub> implements GoConstDefinition {

  public GoConstDefinitionImpl(ASTNode node) {
    super(node);
  }

  public GoConstDefinitionImpl(GoConstDefinitionStub stub, IStubElementType nodeType) {
    super(stub, nodeType);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof GoVisitor) ((GoVisitor)visitor).visitConstDefinition(this);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public PsiElement getIdentifier() {
    return findNotNullChildByType(IDENTIFIER);
  }

}
