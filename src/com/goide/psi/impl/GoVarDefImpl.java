package com.goide.psi.impl;

import com.goide.psi.GoVarDefinition;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import org.jetbrains.annotations.NotNull;

public abstract class GoVarDefImpl extends GoNamedElementImpl implements GoVarDefinition {
  public GoVarDefImpl(ASTNode node) {
    super(node);
  }

  @NotNull
  @Override
  public PsiElement getNameIdentifier() {
    return getIdentifier();
  }

  @Override
  public String getName() {
    return getIdentifier().getText();
  }

  @Override
  public int getTextOffset() {
    return getIdentifier().getTextOffset();
  }

  @Override
  public boolean processDeclarations(@NotNull PsiScopeProcessor processor,
                                     @NotNull ResolveState state,
                                     PsiElement lastParent,
                                     @NotNull PsiElement place) {
    return processor.execute(this, state);
  }
}
