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

package com.goide.stubs;

import com.goide.GoFileElementType;
import com.goide.psi.GoFile;
import com.intellij.psi.stubs.PsiFileStubImpl;
import com.intellij.psi.tree.IStubFileElementType;
import com.intellij.util.io.StringRef;
import org.jetbrains.annotations.NotNull;

public class GoFileStub extends PsiFileStubImpl<GoFile> {
  private final StringRef myPackageName;

  public GoFileStub(@NotNull GoFile file) {
    super(file);
    myPackageName = StringRef.fromNullableString(file.getPackageName());
  }

  public GoFileStub(GoFile file, StringRef packageName) {
    super(file);
    myPackageName = packageName;
  }

  public String getPackageName() {
    return myPackageName.getString();
  }

  @Override
  public IStubFileElementType getType() {
    return GoFileElementType.INSTANCE;
  }
}
