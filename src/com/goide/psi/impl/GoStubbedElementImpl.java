package com.goide.psi.impl;

import com.goide.psi.GoCompositeElement;
import com.goide.psi.GoFile;
import com.intellij.extapi.psi.StubBasedPsiElementBase;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubBase;
import org.jetbrains.annotations.NotNull;

public abstract class GoStubbedElementImpl<T extends StubBase<?>> extends StubBasedPsiElementBase<T> implements GoCompositeElement {
  public GoStubbedElementImpl(@NotNull T stub, @NotNull IStubElementType nodeType) {
    super(stub, nodeType);
  }

  public GoStubbedElementImpl(@NotNull ASTNode node) {
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
    return GoCompositeElementImpl.precessDeclarationDefault(this, processor, state, lastParent, place);
  }

  @NotNull
  @Override
  public GoFile getContainingFile() {
    return (GoFile)super.getContainingFile();
  }
}
