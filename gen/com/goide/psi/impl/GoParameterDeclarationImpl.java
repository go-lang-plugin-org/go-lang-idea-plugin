// This is a generated file. Not intended for manual editing.
package com.goide.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static com.goide.GoTypes.*;
import com.goide.stubs.GoParameterDeclarationStub;
import com.goide.psi.*;
import com.intellij.psi.stubs.IStubElementType;

public class GoParameterDeclarationImpl extends GoStubbedElementImpl<GoParameterDeclarationStub> implements GoParameterDeclaration {

  public GoParameterDeclarationImpl(ASTNode node) {
    super(node);
  }

  public GoParameterDeclarationImpl(GoParameterDeclarationStub stub, IStubElementType nodeType) {
    super(stub, nodeType);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof GoVisitor) ((GoVisitor)visitor).visitParameterDeclaration(this);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<GoParamDefinition> getParamDefinitionList() {
    return findChildrenByClass(GoParamDefinition.class, com.goide.stubs.GoParamDefinitionStub.class);
  }

  @Override
  @NotNull
  public GoType getType() {
    return findNotNullChildByClass(GoType.class, com.goide.stubs.GoTypeStub.class);
  }

  @Override
  @Nullable
  public PsiElement getTripleDot() {
    return findChildByType(TRIPLE_DOT);
  }

}
