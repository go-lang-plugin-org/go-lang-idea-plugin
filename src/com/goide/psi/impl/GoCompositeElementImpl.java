package com.goide.psi.impl;

import com.goide.psi.GoCompositeElement;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import org.jetbrains.annotations.NotNull;

public class GoCompositeElementImpl extends ASTWrapperPsiElement implements GoCompositeElement {
  public GoCompositeElementImpl(@NotNull ASTNode node) {
    super(node);
  }

  @Override
  public String toString() {
    return getNode().getElementType().toString();
  }

  @Override
  public boolean processDeclarations(@NotNull PsiScopeProcessor processor,
                                     @NotNull ResolveState state,
                                     PsiElement lastParent,
                                     @NotNull PsiElement place) {
    return precessDeclarationDefault(this, processor, state, lastParent, place);
  }

  public static boolean precessDeclarationDefault(GoCompositeElement o,
                                                  PsiScopeProcessor processor,
                                                  ResolveState state,
                                                  PsiElement lastParent,
                                                  PsiElement place) {
    return processor.execute(o, state) && ResolveUtil.processChildren(o, processor, state, lastParent, place);
  }
}
