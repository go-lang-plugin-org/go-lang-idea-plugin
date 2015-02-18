/*
 * Copyright 2013-2014 Sergey Ignatov, Alexander Zolotov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

  @Nullable
  @Override
  public String getText() {
    T stub = getStub();
    if (stub instanceof TextHolder) {
      String text = ((TextHolder)stub).getText();
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
      return null;
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

  @Override
  public boolean shouldGoDeeper() {
    return true;
  }
}
