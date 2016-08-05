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

import com.goide.psi.GoPackageClause;
import com.goide.psi.impl.GoPackageClauseImpl;
import com.goide.stubs.GoPackageClauseStub;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class GoPackageClauseStubElementType extends GoStubElementType<GoPackageClauseStub, GoPackageClause> {
  public static final GoPackageClauseStubElementType INSTANCE = new GoPackageClauseStubElementType();

  private GoPackageClauseStubElementType() {
    super("PACKAGE_CLAUSE");
  }

  @NotNull
  @Override
  public GoPackageClause createPsi(@NotNull GoPackageClauseStub stub) {
    return new GoPackageClauseImpl(stub, this);
  }

  @NotNull
  @Override
  public GoPackageClauseStub createStub(@NotNull GoPackageClause psi, StubElement parentStub) {
    return new GoPackageClauseStub(parentStub, this, psi.getName());
  }

  @Override
  public void serialize(@NotNull GoPackageClauseStub stub, @NotNull StubOutputStream dataStream) throws IOException {
    dataStream.writeName(stub.getName());
  }

  @NotNull
  @Override
  public GoPackageClauseStub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
    return new GoPackageClauseStub(parentStub, this, dataStream.readName());
  }
}
