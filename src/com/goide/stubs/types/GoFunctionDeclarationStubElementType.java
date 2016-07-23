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

import com.goide.psi.GoFunctionDeclaration;
import com.goide.psi.GoNamedElement;
import com.goide.psi.impl.GoFunctionDeclarationImpl;
import com.goide.stubs.GoFunctionDeclarationStub;
import com.goide.stubs.index.GoFunctionIndex;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubIndexKey;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import com.intellij.util.ArrayFactory;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

public class GoFunctionDeclarationStubElementType extends GoNamedStubElementType<GoFunctionDeclarationStub, GoFunctionDeclaration> {
  public static final GoFunctionDeclaration[] EMPTY_ARRAY = new GoFunctionDeclaration[0];

  public static final ArrayFactory<GoFunctionDeclaration> ARRAY_FACTORY =
    count -> count == 0 ? EMPTY_ARRAY : new GoFunctionDeclaration[count];
  
  private static final ArrayList<StubIndexKey<String, ? extends GoNamedElement>> EXTRA_KEYS =
    ContainerUtil.newArrayList(GoFunctionIndex.KEY);

  public GoFunctionDeclarationStubElementType(@NotNull String name) {
    super(name);
  }

  @NotNull
  @Override
  public GoFunctionDeclaration createPsi(@NotNull GoFunctionDeclarationStub stub) {
    return new GoFunctionDeclarationImpl(stub, this);
  }

  @NotNull
  @Override
  public GoFunctionDeclarationStub createStub(@NotNull GoFunctionDeclaration psi, StubElement parentStub) {
    return new GoFunctionDeclarationStub(parentStub, this, psi.getName(), psi.isPublic());
  }

  @Override
  public void serialize(@NotNull GoFunctionDeclarationStub stub, @NotNull StubOutputStream dataStream) throws IOException {
    dataStream.writeName(stub.getName());
    dataStream.writeBoolean(stub.isPublic());
  }

  @NotNull
  @Override
  public GoFunctionDeclarationStub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
    return new GoFunctionDeclarationStub(parentStub, this, dataStream.readName(), dataStream.readBoolean());
  }

  @NotNull
  @Override
  protected Collection<StubIndexKey<String, ? extends GoNamedElement>> getExtraIndexKeys() {
    return EXTRA_KEYS;
  }
}
