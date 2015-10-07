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

import com.goide.psi.GoResult;
import com.goide.psi.impl.GoResultImpl;
import com.goide.stubs.GoResultStub;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class GoResultStubElementType extends GoStubElementType<GoResultStub, GoResult> {
  public GoResultStubElementType(@NotNull String name) {
    super(name);
  }

  @NotNull
  @Override
  public GoResult createPsi(@NotNull GoResultStub stub) {
    return new GoResultImpl(stub, this);
  }

  @NotNull
  @Override
  public GoResultStub createStub(@NotNull GoResult psi, StubElement parentStub) {
    return new GoResultStub(parentStub, this, psi.getText());
  }

  @Override
  public void serialize(@NotNull GoResultStub stub, @NotNull StubOutputStream dataStream) throws IOException {
    dataStream.writeName(stub.getText());
  }

  @NotNull
  @Override
  public GoResultStub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
    return new GoResultStub(parentStub, this, dataStream.readName());
  }
}
