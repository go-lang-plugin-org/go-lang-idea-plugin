// This is a generated file. Not intended for manual editing.
package com.goide.psi.impl;

import com.goide.psi.GoImportDeclaration;
import com.goide.psi.GoImportSpec;
import com.goide.psi.GoVisitor;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static com.goide.GoTypes.*;

public class GoImportDeclarationImpl extends GoCompositeElementImpl implements GoImportDeclaration {

  public GoImportDeclarationImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof GoVisitor) ((GoVisitor)visitor).visitImportDeclaration(this);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<GoImportSpec> getImportSpecList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, GoImportSpec.class);
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

  @Override
  @NotNull
  public PsiElement getImport() {
    return findNotNullChildByType(IMPORT);
  }

  @NotNull
  public GoImportSpec addImportSpec(String packagePath, String alias) {
    return GoPsiImplUtil.addImportSpec(this, packagePath, alias);
  }

}
