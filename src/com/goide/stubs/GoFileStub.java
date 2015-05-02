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

package com.goide.stubs;

import com.goide.GoFileElementType;
import com.goide.psi.GoFile;
import com.intellij.psi.stubs.PsiFileStubImpl;
import com.intellij.psi.tree.IStubFileElementType;
import com.intellij.util.io.StringRef;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GoFileStub extends PsiFileStubImpl<GoFile> {
  private final StringRef myPackageName;
  private final StringRef myBuildFlags;

  public GoFileStub(@NotNull GoFile file) {
    this(file, StringRef.fromNullableString(file.getPackageName()), StringRef.fromNullableString(file.getBuildFlags()));
  }

  public GoFileStub(@Nullable GoFile file, StringRef packageName, StringRef buildFlags) {
    super(file);
    myPackageName = packageName;
    myBuildFlags = buildFlags;
  }

  @Nullable
  public String getPackageName() {
    return myPackageName.getString();
  }

  @Override
  public IStubFileElementType getType() {
    return GoFileElementType.INSTANCE;
  }

  @Nullable
  public String getBuildFlags() {
    return myBuildFlags.getString();
  }
}
