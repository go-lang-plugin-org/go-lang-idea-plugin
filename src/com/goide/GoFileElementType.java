/*
 * Copyright 2013-2015 Sergey Ignatov, Alexander Zolotov, Mihai Toader, Florin Patan
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

package com.goide;

import com.goide.psi.GoFile;
import com.goide.stubs.GoFileStub;
import com.goide.stubs.index.GoPackagesIndex;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiFile;
import com.intellij.psi.StubBuilder;
import com.intellij.psi.stubs.*;
import com.intellij.psi.tree.IStubFileElementType;
import com.intellij.util.io.StringRef;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class GoFileElementType extends IStubFileElementType<GoFileStub> {
  public static final IStubFileElementType INSTANCE = new GoFileElementType();
  public static final int VERSION = 10;

  private GoFileElementType() {
    super("GO_FILE", GoLanguage.INSTANCE);
  }

  @Override
  public int getStubVersion() {
    return VERSION;
  }

  @NotNull
  @Override
  public StubBuilder getBuilder() {
    return new DefaultStubBuilder() {
      @NotNull
      @Override
      protected StubElement createStubForFile(@NotNull PsiFile file) {
        if (file instanceof GoFile) {
          return new GoFileStub((GoFile)file);
        }
        return super.createStubForFile(file);
      }
    };
  }

  @Override
  public void indexStub(@NotNull GoFileStub stub, @NotNull IndexSink sink) {
    super.indexStub(stub, sink);
    String packageName = stub.getPackageName();
    if (StringUtil.isNotEmpty(packageName)) {
      sink.occurrence(GoPackagesIndex.KEY, packageName);
    }
  }

  @Override
  public void serialize(@NotNull GoFileStub stub, @NotNull StubOutputStream dataStream) throws IOException {
    dataStream.writeName(stub.getPackageName());
    dataStream.writeUTF(StringUtil.notNullize(stub.getBuildFlags()));
  }

  @NotNull
  @Override
  public GoFileStub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
    return new GoFileStub(null, dataStream.readName(), StringRef.fromNullableString(StringUtil.nullize(dataStream.readUTF())));
  }

  @NotNull
  @Override
  public String getExternalId() {
    return "go.FILE";
  }
}
