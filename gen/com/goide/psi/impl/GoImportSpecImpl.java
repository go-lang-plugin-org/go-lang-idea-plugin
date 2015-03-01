// This is a generated file. Not intended for manual editing.
package com.goide.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static com.goide.GoTypes.*;
import com.intellij.extapi.psi.StubBasedPsiElementBase;
import com.goide.stubs.GoImportSpecStub;
import com.goide.psi.*;
import com.intellij.psi.stubs.IStubElementType;

public class GoImportSpecImpl extends StubBasedPsiElementBase<GoImportSpecStub> implements GoImportSpec {

  public GoImportSpecImpl(ASTNode node) {
    super(node);
  }

  public GoImportSpecImpl(GoImportSpecStub stub, IStubElementType nodeType) {
    super(stub, nodeType);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof GoVisitor) ((GoVisitor)visitor).visitImportSpec(this);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public GoImportString getImportString() {
    return findNotNullChildByClass(GoImportString.class);
  }

  @Override
  @Nullable
  public PsiElement getDot() {
    return findChildByType(DOT);
  }

  @Override
  @Nullable
  public PsiElement getIdentifier() {
    return findChildByType(IDENTIFIER);
  }

  public String getAlias() {
    return GoPsiImplUtil.getAlias(this);
  }

  public String getLocalPackageName() {
    return GoPsiImplUtil.getLocalPackageName(this);
  }

  public boolean shouldGoDeeper() {
    return GoPsiImplUtil.shouldGoDeeper(this);
  }

  public boolean isForSideEffects() {
    return GoPsiImplUtil.isForSideEffects(this);
  }

}
