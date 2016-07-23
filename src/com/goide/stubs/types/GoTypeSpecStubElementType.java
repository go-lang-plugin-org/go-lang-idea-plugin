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

import com.goide.psi.GoNamedElement;
import com.goide.psi.GoTypeSpec;
import com.goide.psi.impl.GoTypeSpecImpl;
import com.goide.stubs.GoTypeSpecStub;
import com.goide.stubs.index.GoTypesIndex;
import com.intellij.lang.ASTNode;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubIndexKey;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import com.intellij.util.ArrayFactory;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Collection;

public class GoTypeSpecStubElementType extends GoNamedStubElementType<GoTypeSpecStub, GoTypeSpec> {
  public static final GoTypeSpec[] EMPTY_ARRAY = new GoTypeSpec[0];

  public static final ArrayFactory<GoTypeSpec> ARRAY_FACTORY = count -> count == 0 ? EMPTY_ARRAY : new GoTypeSpec[count];
  
  public GoTypeSpecStubElementType(@NotNull String name) {
    super(name);
  }

  @NotNull
  @Override
  public GoTypeSpec createPsi(@NotNull GoTypeSpecStub stub) {
    return new GoTypeSpecImpl(stub, this);
  }

  @NotNull
  @Override
  public GoTypeSpecStub createStub(@NotNull GoTypeSpec psi, StubElement parentStub) {
    return new GoTypeSpecStub(parentStub, this, psi.getName(), psi.isPublic());
  }

  @Override
  public void serialize(@NotNull GoTypeSpecStub stub, @NotNull StubOutputStream dataStream) throws IOException {
    dataStream.writeName(stub.getName());
    dataStream.writeBoolean(stub.isPublic());
  }

  @NotNull
  @Override
  public GoTypeSpecStub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
    return new GoTypeSpecStub(parentStub, this, dataStream.readName(), dataStream.readBoolean());
  }

  @NotNull
  @Override
  protected Collection<StubIndexKey<String, ? extends GoNamedElement>> getExtraIndexKeys() {
    return ContainerUtil.list(GoTypesIndex.KEY);
  }

  @Override
  protected boolean shouldCreateStubInBlock(ASTNode node) {
    return true;
  }
}
