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

public class GoTopLevelDeclImpl extends GoCompositeElementImpl implements GoTopLevelDecl {

  public GoTopLevelDeclImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof GoVisitor) ((GoVisitor)visitor).visitTopLevelDecl(this);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public GoDeclarationStatement getDeclarationStatement() {
    return findChildByClass(GoDeclarationStatement.class);
  }

  @Override
  @Nullable
  public GoFunctionDecl getFunctionDecl() {
    return findChildByClass(GoFunctionDecl.class);
  }

  @Override
  @Nullable
  public GoMethodDecl getMethodDecl() {
    return findChildByClass(GoMethodDecl.class);
  }

}
