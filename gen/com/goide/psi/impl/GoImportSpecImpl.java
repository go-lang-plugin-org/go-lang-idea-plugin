// This is a generated file. Not intended for manual editing.
package com.goide.psi.impl;

import com.goide.psi.GoImportPath;
import com.goide.psi.GoImportSpec;
import com.goide.psi.GoVisitor;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.goide.GoTypes.IDENTIFIER;

public class GoImportSpecImpl extends GoCompositeElementImpl implements GoImportSpec {

  public GoImportSpecImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof GoVisitor) ((GoVisitor)visitor).visitImportSpec(this);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public GoImportPath getImportPath() {
    return findNotNullChildByClass(GoImportPath.class);
  }

  @Override
  @Nullable
  public PsiElement getIdentifier() {
    return findChildByType(IDENTIFIER);
  }

}
