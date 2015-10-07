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

import com.goide.psi.GoAnonymousFieldDefinition;
import com.goide.psi.impl.GoAnonymousFieldDefinitionImpl;
import com.goide.stubs.GoAnonymousFieldDefinitionStub;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class GoAnonymousFieldDefinitionStubElementType extends GoNamedStubElementType<GoAnonymousFieldDefinitionStub, GoAnonymousFieldDefinition> {
  public GoAnonymousFieldDefinitionStubElementType(@NotNull String name) {
    super(name);
  }

  @NotNull
  @Override
  public GoAnonymousFieldDefinition createPsi(@NotNull GoAnonymousFieldDefinitionStub stub) {
    return new GoAnonymousFieldDefinitionImpl(stub, this);
  }

  @NotNull
  @Override
  public GoAnonymousFieldDefinitionStub createStub(@NotNull GoAnonymousFieldDefinition psi, StubElement parentStub) {
    return new GoAnonymousFieldDefinitionStub(parentStub, this, psi.getName(), psi.isPublic());
  }

  @Override
  public void serialize(@NotNull GoAnonymousFieldDefinitionStub stub, @NotNull StubOutputStream dataStream) throws IOException {
    dataStream.writeName(stub.getName());
    dataStream.writeBoolean(stub.isPublic());
  }

  @NotNull
  @Override
  public GoAnonymousFieldDefinitionStub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
    return new GoAnonymousFieldDefinitionStub(parentStub, this, dataStream.readName(), dataStream.readBoolean());
  }
}
