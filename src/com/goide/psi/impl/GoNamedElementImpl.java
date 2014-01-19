package com.goide.psi.impl;

import com.goide.psi.GoCompositeElement;
import com.goide.psi.GoNamedElement;
import com.goide.psi.GoType;
import com.goide.stubs.GoNamedStub;
import com.intellij.extapi.psi.StubBasedPsiElementBase;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class GoNamedElementImpl<T extends GoNamedStub<?>> extends StubBasedPsiElementBase<T> implements GoCompositeElement, GoNamedElement {

  public GoNamedElementImpl(@NotNull T stub, @NotNull IStubElementType nodeType) {
    super(stub, nodeType);
  }

  public GoNamedElementImpl(@NotNull ASTNode node) {
    super(node);
  }

  public boolean isPublic() {
    T stub = getStub();
    return stub != null ? stub.isPublic() : StringUtil.isCapitalized(getName());
  }

  @Nullable
  @Override
  public PsiElement getNameIdentifier() {
    return getIdentifier();
  }

  @Nullable
  @Override
  public String getName() {
    T stub = getStub();
    if (stub != null) {
      return stub.getName();
    }
    PsiElement identifier = getIdentifier();
    return identifier != null ? identifier.getText() : null;
  }

  @Override
  public int getTextOffset() {
    PsiElement identifier = getIdentifier();
    return identifier != null ? identifier.getTextOffset() : 0;
  }

  @Override
  public PsiElement setName(@NonNls @NotNull String newName) throws IncorrectOperationException {
    PsiElement identifier = getIdentifier();
    if (identifier != null) {
      identifier.replace(GoElementFactory.createIdentifierFromText(getProject(), newName));
    }
    return this;
  }

  @Nullable
  @Override
  public GoType getGoType() {
    return getType(this);
  }

  @Nullable
  public static GoType getType(GoNamedElement o) {
    return PsiTreeUtil.getNextSiblingOfType(o, GoType.class);
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
}
