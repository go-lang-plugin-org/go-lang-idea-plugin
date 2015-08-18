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

import com.goide.psi.GoMethodSpec;
import com.goide.psi.impl.GoMethodSpecImpl;
import com.goide.psi.impl.GoPsiImplUtil;
import com.goide.stubs.GoMethodSpecStub;
import com.goide.stubs.index.GoMethodFingerprintIndex;
import com.intellij.psi.stubs.IndexSink;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class GoMethodSpecStubElementType extends GoNamedStubElementType<GoMethodSpecStub, GoMethodSpec> {
  public GoMethodSpecStubElementType(@NotNull String name) {
    super(name);
  }

  @Override
  public void indexStub(@NotNull GoMethodSpecStub stub, @NotNull IndexSink sink) {
    super.indexStub(stub, sink);
    String name = stub.getName();
    int arity = stub.getArity();
    if (name != null && arity >= 0) {
      sink.occurrence(GoMethodFingerprintIndex.KEY, name + "/" + arity);
    }
  }

  @NotNull
  @Override
  public GoMethodSpec createPsi(@NotNull GoMethodSpecStub stub) {
    return new GoMethodSpecImpl(stub, this);
  }

  @NotNull
  @Override
  public GoMethodSpecStub createStub(@NotNull GoMethodSpec psi, StubElement parentStub) {
    int arity = GoPsiImplUtil.getArity(psi.getSignature());
    return new GoMethodSpecStub(parentStub, this, psi.getName(), psi.isPublic(), arity);
  }

  @Override
  public void serialize(@NotNull GoMethodSpecStub stub, @NotNull StubOutputStream dataStream) throws IOException {
    dataStream.writeName(stub.getName());
    dataStream.writeBoolean(stub.isPublic());
    dataStream.writeVarInt(stub.getArity());
  }

  @NotNull
  @Override
  public GoMethodSpecStub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
    return new GoMethodSpecStub(parentStub, this, dataStream.readName(), dataStream.readBoolean(), dataStream.readVarInt());
  }
}
