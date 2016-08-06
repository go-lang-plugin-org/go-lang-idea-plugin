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

import com.goide.psi.GoType;
import com.goide.psi.GoTypeSpec;
import com.goide.stubs.GoTypeStub;
import com.intellij.lang.ASTNode;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public abstract class GoTypeStubElementType extends GoStubElementType<GoTypeStub, GoType> {
  protected GoTypeStubElementType(@NotNull String name) {
    super(name);
  }

  @NotNull
  @Override
  public GoTypeStub createStub(@NotNull GoType psi, StubElement parentStub) {
    return new GoTypeStub(parentStub, this, psi.getText());
  }

  @Override
  public void serialize(@NotNull GoTypeStub stub, @NotNull StubOutputStream dataStream) throws IOException {
    dataStream.writeName(stub.getText());
  }

  @Override
  protected boolean shouldCreateStubInBlock(ASTNode node) {
    return PsiTreeUtil.getParentOfType(node.getPsi(), GoTypeSpec.class) != null || super.shouldCreateStubInBlock(node);
  }

  @NotNull
  @Override
  public GoTypeStub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
    return new GoTypeStub(parentStub, this, dataStream.readName());
  }
}
