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

package com.goide.psi.impl.imports;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.FileContextProvider;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileSystemItem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;

public class GoFileContextProvider extends FileContextProvider {
  @Override
  protected boolean isAvailable(@NotNull PsiFile file) {
    VirtualFile virtualFile = file.getVirtualFile();
    return virtualFile != null && new GoImportReferenceHelper().isMine(file.getProject(), virtualFile);
  }

  @NotNull
  @Override
  public Collection<PsiFileSystemItem> getContextFolders(@NotNull PsiFile file) {
    VirtualFile virtualFile = file.getVirtualFile();
    return virtualFile != null
           ? new GoImportReferenceHelper().getContexts(file.getProject(), virtualFile)
           : Collections.<PsiFileSystemItem>emptyList();
  }

  @Nullable
  @Override
  public PsiFile getContextFile(PsiFile file) {
    return null;
  }
}
