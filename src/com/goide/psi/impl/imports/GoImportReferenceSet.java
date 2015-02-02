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

package com.goide.psi.impl.imports;

import com.goide.psi.GoImportString;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiFileSystemItem;
import com.intellij.psi.impl.source.resolve.reference.impl.providers.FileReference;
import com.intellij.psi.impl.source.resolve.reference.impl.providers.FileReferenceSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GoImportReferenceSet extends FileReferenceSet {
  public GoImportReferenceSet(@NotNull GoImportString importString) {
    super(importString.getPath(), importString, importString.getPathTextRange().getStartOffset(), null, true);
  }

  @Override
  protected Condition<PsiFileSystemItem> getReferenceCompletionFilter() {
    return DIRECTORY_FILTER;
  }

  @Nullable
  @Override
  public PsiFileSystemItem resolve() {
    if (isAbsolutePathReference()) {
      return null;
    }

    return super.resolve();
  }

  @Override
  public boolean absoluteUrlNeedsStartSlash() {
    return false;
  }

  @NotNull
  @Override
  public FileReference createFileReference(TextRange range, int index, String text) {
    return new GoImportReference(this, range, index, text);
  }
}
