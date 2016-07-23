/*
 * Copyright 2013-2016 Sergey Ignatov, Alexander Zolotov, Florin Patan
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

import com.goide.psi.*;
import com.goide.stubs.GoTypeStub;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.light.LightElement;
import com.intellij.psi.stubs.IStubElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class GoLightType<E extends GoCompositeElement> extends LightElement implements GoType {
  @NotNull protected final E myElement;

  protected GoLightType(@NotNull E e) {
    super(e.getManager(), e.getLanguage());
    myElement = e;
    setNavigationElement(e);
  }

  @Nullable
  @Override
  public GoTypeReferenceExpression getTypeReferenceExpression() {
    return null;
  }

  @Override
  public boolean shouldGoDeeper() {
    return false;
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + "{" + myElement + "}";
  }

  @Override
  public IStubElementType getElementType() {
    return null;
  }

  @Override
  public GoTypeStub getStub() {
    return null;
  }

  @NotNull
  @Override
  public GoType getUnderlyingType() {
    return GoPsiImplUtil.getUnderlyingType(this);
  }

  static class LightPointerType extends GoLightType<GoType> implements GoPointerType {
    protected LightPointerType(@NotNull GoType o) {
      super(o);
    }

    @Override
    public String getText() {
      return "*" + myElement.getText();
    }

    @Nullable
    @Override
    public GoType getType() {
      return myElement;
    }

    @NotNull
    @Override
    public PsiElement getMul() {
      return myElement; // todo: mock it?
    }
  }

  static class LightTypeList extends GoLightType<GoCompositeElement> implements GoTypeList {
    @NotNull private final List<GoType> myTypes;

    public LightTypeList(@NotNull GoCompositeElement o, @NotNull List<GoType> types) {
      super(o);
      myTypes = types;
    }

    @NotNull
    @Override
    public List<GoType> getTypeList() {
      return myTypes;
    }

    @Override
    public String toString() {
      return "MyGoTypeList{myTypes=" + myTypes + '}';
    }

    @Override
    public String getText() {
      return StringUtil.join(getTypeList(), PsiElement::getText, ", ");
    }
  }

  static class LightFunctionType extends GoLightType<GoSignatureOwner> implements GoFunctionType {
    public LightFunctionType(@NotNull GoSignatureOwner o) {
      super(o);
    }

    @Nullable
    @Override
    public GoSignature getSignature() {
      return myElement.getSignature();
    }

    @NotNull
    @Override
    public PsiElement getFunc() {
      return myElement instanceof GoFunctionOrMethodDeclaration ? ((GoFunctionOrMethodDeclaration)myElement).getFunc() : myElement;
    }

    @Override
    public String getText() {
      GoSignature signature = myElement.getSignature();
      return "func " + (signature != null ? signature.getText() : "<null>");
    }
  }

  static class LightArrayType extends GoLightType<GoType> implements GoArrayOrSliceType {
    protected LightArrayType(GoType type) {
      super(type);
    }

    @Override
    public String getText() {
      return "[]" + myElement.getText();
    }

    @Nullable
    @Override
    public GoExpression getExpression() {
      return null;
    }

    @Nullable
    @Override
    public GoType getType() {
      return myElement;
    }

    @NotNull
    @Override
    public PsiElement getLbrack() {
      //noinspection ConstantConditions
      return null; // todo: mock?
    }

    @Nullable
    @Override
    public PsiElement getRbrack() {
      return null;
    }

    @Nullable
    @Override
    public PsiElement getTripleDot() {
      return null;
    }
  }
}
