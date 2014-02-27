// This is a generated file. Not intended for manual editing.
package com.goide.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static com.goide.GoTypes.*;
import com.goide.psi.*;

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
