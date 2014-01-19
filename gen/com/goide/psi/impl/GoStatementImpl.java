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
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;

public class GoStatementImpl extends GoCompositeElementImpl implements GoStatement {

  public GoStatementImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof GoVisitor) ((GoVisitor)visitor).visitStatement(this);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public GoBlock getBlock() {
    return findChildByClass(GoBlock.class);
  }

  @Override
  @Nullable
  public GoConstDeclaration getConstDeclaration() {
    return findChildByClass(GoConstDeclaration.class);
  }

  @Override
  @Nullable
  public GoTypeDeclaration getTypeDeclaration() {
    return findChildByClass(GoTypeDeclaration.class);
  }

  @Override
  @Nullable
  public GoVarDeclaration getVarDeclaration() {
    return findChildByClass(GoVarDeclaration.class);
  }

  public boolean processDeclarations(PsiScopeProcessor processor, ResolveState state, PsiElement lastParent, PsiElement place) {
    return GoPsiImplUtil.processDeclarations(this, processor, state, lastParent, place);
  }

}
