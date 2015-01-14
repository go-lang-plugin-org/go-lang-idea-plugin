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

import com.goide.GoIcons;
import com.goide.psi.*;
import com.goide.stubs.GoNamedStub;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.search.SearchScope;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public abstract class GoNamedElementImpl<T extends GoNamedStub<?>> extends GoStubbedElementImpl<T> implements GoCompositeElement, GoNamedElement {

  public GoNamedElementImpl(@NotNull T stub, @NotNull IStubElementType nodeType) {
    super(stub, nodeType);
  }

  public GoNamedElementImpl(@NotNull ASTNode node) {
    super(node);
  }

  public boolean isPublic() {
    if (GoPsiImplUtil.builtin(this)) return true;
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
    return identifier != null ? identifier.getTextOffset() : super.getTextOffset();
  }

  @NotNull
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
  public static GoType getType(GoNamedElement o) { // todo: use stubs
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

  @Nullable
  @Override
  public Icon getIcon(int flags) {
    if (this instanceof GoMethodDeclaration) return GoIcons.METHOD;
    if (this instanceof GoFunctionDeclaration) return GoIcons.FUNCTION;
    if (this instanceof GoTypeSpec) return GoIcons.TYPE;
    if (this instanceof GoVarDefinition) return GoIcons.VARIABLE;
    if (this instanceof GoConstDefinition) return GoIcons.CONST;
    if (this instanceof GoFieldDefinition) return GoIcons.FIELD;
    if (this instanceof GoMethodSpec) return GoIcons.METHOD;
    if (this instanceof GoAnonymousFieldDefinition) return GoIcons.FIELD;
    if (this instanceof GoParamDefinition) return GoIcons.PARAMETER;
    return super.getIcon(flags);
  }

  @NotNull
  @Override
  public SearchScope getUseScope() {
    return isPublic() ? super.getUseScope() : GoPsiImplUtil.cretePackageScope(getContainingFile());
  }

  @Override
  public boolean isBlank() {
    return StringUtil.equals(getName(), "_");
  }
}
