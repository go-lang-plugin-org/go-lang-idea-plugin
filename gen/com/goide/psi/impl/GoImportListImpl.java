// This is a generated file. Not intended for manual editing.
package com.goide.psi.impl;

import com.goide.psi.GoImportDeclaration;
import com.goide.psi.GoImportList;
import com.goide.psi.GoImportSpec;
import com.goide.psi.GoVisitor;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class GoImportListImpl extends GoCompositeElementImpl implements GoImportList {

  public GoImportListImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof GoVisitor) ((GoVisitor)visitor).visitImportList(this);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<GoImportDeclaration> getImportDeclarationList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, GoImportDeclaration.class);
  }

  @NotNull
  public GoImportSpec addImport(String packagePath, String alias) {
    return GoPsiImplUtil.addImport(this, packagePath, alias);
  }

}
