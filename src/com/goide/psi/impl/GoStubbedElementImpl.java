package com.goide.psi.impl;

import com.goide.psi.GoCompositeElement;
import com.goide.psi.GoFile;
import com.goide.stubs.TextHolder;
import com.intellij.extapi.psi.StubBasedPsiElementBase;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.SmartList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class GoStubbedElementImpl<T extends StubBase<?> & TextHolder> extends StubBasedPsiElementBase<T> implements GoCompositeElement {
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
  public String getText() {
    T stub = getStub();
    if (stub != null) {
      String text = stub.getText();
      if (text != null) return text;
    }
    return super.getText();
  }

  @Override
  public boolean processDeclarations(@NotNull PsiScopeProcessor processor,
                                     @NotNull ResolveState state,
                                     PsiElement lastParent,
                                     @NotNull PsiElement place) {
    return GoCompositeElementImpl.precessDeclarationDefault(this, processor, state, lastParent, place);
  }
  
  @Nullable
  protected <C, S> C findChildByClass(Class<C> c, Class<S> s) {
    T stub = getStub();
    if (stub != null) {
      for (StubElement stubElement : stub.getChildrenStubs()) {
        if (s.isInstance(stubElement)) {
          //noinspection unchecked
          return (C)stubElement.getPsi();
        }
      }
    }
    return super.findChildByClass(c);
  }

  @Nullable
  protected <C, S> C findNotNullChildByClass(Class<C> c, Class<S> s) {
    return notNullChild(findChildByClass(c, s));
  }

  @NotNull
  protected <C extends PsiElement, S> List<C> findChildrenByClass(Class<C> c, Class<S> s) {
    T stub = getStub();
    if (stub != null) {
      List<C> result = new SmartList<C>();
      for (StubElement stubElement : stub.getChildrenStubs()) {
        if (s.isInstance(stubElement)) {
          //noinspection unchecked
          result.add((C)stubElement.getPsi());
        }
      }
      return result;
    }
    return PsiTreeUtil.getChildrenOfTypeAsList(this, c);
  }

  @NotNull
  @Override
  public GoFile getContainingFile() {
    return (GoFile)super.getContainingFile();
  }
}
