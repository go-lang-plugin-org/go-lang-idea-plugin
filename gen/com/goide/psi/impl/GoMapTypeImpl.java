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

// This is a generated file. Not intended for manual editing.
package com.goide.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.goide.psi.GoPsiTreeUtil;
import static com.goide.GoTypes.*;
import com.goide.psi.*;
import com.goide.stubs.GoTypeStub;
import com.intellij.psi.stubs.IStubElementType;

public class GoMapTypeImpl extends GoTypeImpl implements GoMapType {

  public GoMapTypeImpl(GoTypeStub stub, IStubElementType nodeType) {
    super(stub, nodeType);
  }

  public GoMapTypeImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull GoVisitor visitor) {
    visitor.visitMapType(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof GoVisitor) accept((GoVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<GoType> getTypeList() {
    return GoPsiTreeUtil.getStubChildrenOfTypeAsList(this, GoType.class);
  }

  @Override
  @Nullable
  public PsiElement getLbrack() {
    return findChildByType(LBRACK);
  }

  @Override
  @Nullable
  public PsiElement getRbrack() {
    return findChildByType(RBRACK);
  }

  @Override
  @NotNull
  public PsiElement getMap() {
    return notNullChild(findChildByType(MAP));
  }

  @Override
  @Nullable
  public GoType getKeyType() {
    List<GoType> p1 = getTypeList();
    return p1.size() < 1 ? null : p1.get(0);
  }

  @Override
  @Nullable
  public GoType getValueType() {
    List<GoType> p1 = getTypeList();
    return p1.size() < 2 ? null : p1.get(1);
  }

}
