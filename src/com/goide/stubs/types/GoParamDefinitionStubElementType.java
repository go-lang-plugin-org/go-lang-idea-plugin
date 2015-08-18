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

package com.goide.stubs.types;

import com.goide.psi.GoParamDefinition;
import com.goide.psi.impl.GoParamDefinitionImpl;
import com.goide.stubs.GoParamDefinitionStub;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class GoParamDefinitionStubElementType extends GoNamedStubElementType<GoParamDefinitionStub, GoParamDefinition> {
  public GoParamDefinitionStubElementType(@NotNull String name) {
    super(name);
  }

  @NotNull
  @Override
  public GoParamDefinition createPsi(@NotNull GoParamDefinitionStub stub) {
    return new GoParamDefinitionImpl(stub, this);
  }

  @NotNull
  @Override
  public GoParamDefinitionStub createStub(@NotNull GoParamDefinition psi, StubElement parentStub) {
    return new GoParamDefinitionStub(parentStub, this, psi.getName(), psi.isPublic());
  }

  @Override
  public void serialize(@NotNull GoParamDefinitionStub stub, @NotNull StubOutputStream dataStream) throws IOException {
    dataStream.writeName(stub.getName());
    dataStream.writeBoolean(stub.isPublic());
  }

  @NotNull
  @Override
  public GoParamDefinitionStub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
    return new GoParamDefinitionStub(parentStub, this, dataStream.readName(), dataStream.readBoolean());
  }

  @Override
  protected boolean shouldIndex() {
    return false;
  }
}
