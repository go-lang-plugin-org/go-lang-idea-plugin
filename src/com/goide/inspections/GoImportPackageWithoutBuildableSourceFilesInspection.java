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

package com.goide.inspections;

import com.goide.GoFileType;
import com.goide.psi.GoFile;
import com.goide.psi.GoImportSpec;
import com.goide.quickfix.GoDeleteImportQuickFix;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.util.CachedValueProvider;
import com.intellij.psi.util.CachedValuesManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GoImportPackageWithoutBuildableSourceFilesInspection extends GoInspectionBase {
  @Override
  protected void checkFile(@NotNull GoFile file, @NotNull ProblemsHolder problemsHolder) {
    for (GoImportSpec importSpec : file.getImports()) {
      PsiDirectory resolve = importSpec.getImportString().resolve();
      if (resolve != null && !hasGoFiles(resolve)) {
        problemsHolder.registerProblem(importSpec, "'" + resolve.getVirtualFile().getPath() + "' has no buildable Go source files",
                                       new GoDeleteImportQuickFix());
      }
    }
  }

  private static boolean hasGoFiles(@NotNull PsiDirectory resolve) {
    return CachedValuesManager.getCachedValue(resolve, new CachedValueProvider<Boolean>() {
      @Nullable
      @Override
      public Result<Boolean> compute() {
        for (VirtualFile virtualFile : resolve.getVirtualFile().getChildren()) {
          if (!virtualFile.isDirectory() && virtualFile.getFileType() == GoFileType.INSTANCE) {
            return Result.create(Boolean.TRUE, resolve);
          }
        }
        return Result.create(Boolean.FALSE, resolve);
      }
    });
  }
}
