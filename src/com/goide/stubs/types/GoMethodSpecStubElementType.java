/*
 * Copyright 2013-2014 Sergey Ignatov, Alexander Zolotov
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

import com.goide.psi.GoMethodSpec;
import com.goide.psi.impl.GoMethodSpecImpl;
import com.goide.stubs.GoMethodSpecStub;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import com.intellij.util.ArrayFactory;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class GoMethodSpecStubElementType extends GoNamedStubElementType<GoMethodSpecStub, GoMethodSpec> {
  public static final GoMethodSpec[] EMPTY_ARRAY = new GoMethodSpec[0];

  public static final ArrayFactory<GoMethodSpec> ARRAY_FACTORY = new ArrayFactory<GoMethodSpec>() {
    @NotNull
    @Override
    public GoMethodSpec[] create(final int count) {
      return count == 0 ? EMPTY_ARRAY : new GoMethodSpec[count];
    }
  };

  public GoMethodSpecStubElementType(@NotNull String name) {
    super(name);
  }

  @NotNull
  @Override
  public GoMethodSpec createPsi(@NotNull GoMethodSpecStub stub) {
    return new GoMethodSpecImpl(stub, this);
  }

  @NotNull
  @Override
  public GoMethodSpecStub createStub(@NotNull GoMethodSpec psi, StubElement parentStub) {
    return new GoMethodSpecStub(parentStub, this, psi.getName(), psi.isPublic());
  }

  @Override
  public void serialize(@NotNull GoMethodSpecStub stub, @NotNull StubOutputStream dataStream) throws IOException {
    dataStream.writeName(stub.getName());
    dataStream.writeBoolean(stub.isPublic());
  }

  @NotNull
  @Override
  public GoMethodSpecStub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
    return new GoMethodSpecStub(parentStub, this, dataStream.readName(), dataStream.readBoolean());
  }
}
