/*
 * Copyright 2013-2015 Sergey Ignatov, Alexander Zolotov, Florin Patan
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
import com.goide.psi.GoType;
import com.goide.psi.GoTypeReferenceExpression;
import com.goide.stubs.GoTypeStub;
import com.intellij.psi.impl.light.LightElement;
import com.intellij.psi.stubs.IStubElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class GoLightType<E extends GoCompositeElement> extends LightElement implements GoType {
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
    return null;
  }

  @Override
  public IStubElementType getElementType() {
    return null;
  }

  @Override
  public GoTypeStub getStub() {
    return null;
  }
}
