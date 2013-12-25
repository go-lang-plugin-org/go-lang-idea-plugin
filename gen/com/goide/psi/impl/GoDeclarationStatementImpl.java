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

public class GoDeclarationStatementImpl extends GoCompositeElementImpl implements GoDeclarationStatement {

  public GoDeclarationStatementImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof GoVisitor) ((GoVisitor)visitor).visitDeclarationStatement(this);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public GoConstDecl getConstDecl() {
    return findChildByClass(GoConstDecl.class);
  }

  @Override
  @Nullable
  public GoTypeDecl getTypeDecl() {
    return findChildByClass(GoTypeDecl.class);
  }

  @Override
  @Nullable
  public GoVarDecl getVarDecl() {
    return findChildByClass(GoVarDecl.class);
  }

}
