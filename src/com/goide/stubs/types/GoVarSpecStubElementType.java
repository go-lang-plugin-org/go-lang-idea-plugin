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

package com.goide.stubs.types;

import com.goide.GoTypes;
import com.goide.psi.GoFunctionOrMethodDeclaration;
import com.goide.psi.GoVarSpec;
import com.goide.psi.impl.GoVarSpecImpl;
import com.goide.stubs.GoVarSpecStub;
import com.intellij.lang.ASTNode;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ArrayFactory;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class GoVarSpecStubElementType extends GoStubElementType<GoVarSpecStub, GoVarSpec> {
  public static final GoVarSpec[] EMPTY_ARRAY = new GoVarSpec[0];

  public static final ArrayFactory<GoVarSpec> ARRAY_FACTORY = count -> count == 0 ? EMPTY_ARRAY : new GoVarSpec[count];
  
  public GoVarSpecStubElementType(@NotNull String name) {
    super(name);
  }

  @NotNull
  @Override
  public GoVarSpec createPsi(@NotNull GoVarSpecStub stub) {
    return new GoVarSpecImpl(stub, this);
  }

  @NotNull
  @Override
  public GoVarSpecStub createStub(@NotNull GoVarSpec psi, StubElement parentStub) {
    return new GoVarSpecStub(parentStub, this);
  }

  @Override
  public boolean shouldCreateStub(ASTNode node) {
    return super.shouldCreateStub(node) &&
           node.getElementType() == GoTypes.VAR_SPEC &&
           PsiTreeUtil.getParentOfType(node.getPsi(), GoFunctionOrMethodDeclaration.class) == null;
  }

  @Override
  public void serialize(@NotNull GoVarSpecStub stub, @NotNull StubOutputStream dataStream) throws IOException {
  }

  @NotNull
  @Override
  public GoVarSpecStub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
    return new GoVarSpecStub(parentStub, this);
  }
}
