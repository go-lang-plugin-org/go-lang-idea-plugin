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

import com.goide.psi.GoFunctionOrMethodDeclaration;
import com.goide.psi.GoType;
import com.goide.stubs.GoFunctionOrMethodDeclarationStub;
import com.intellij.lang.ASTNode;
import com.intellij.psi.ResolveState;
import com.intellij.psi.stubs.IStubElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

abstract public class GoFunctionOrMethodDeclarationImpl<T extends GoFunctionOrMethodDeclarationStub<?>> extends GoNamedElementImpl<T>
  implements GoFunctionOrMethodDeclaration {
  public GoFunctionOrMethodDeclarationImpl(@NotNull T stub, @NotNull IStubElementType nodeType) {
    super(stub, nodeType);
  }

  public GoFunctionOrMethodDeclarationImpl(@NotNull ASTNode node) {
    super(node);
  }

  @Override
  @Nullable
  public GoType getGoTypeInner(@Nullable ResolveState context) {
    return GoPsiImplUtil.getGoTypeInner(this, context);
  }
}