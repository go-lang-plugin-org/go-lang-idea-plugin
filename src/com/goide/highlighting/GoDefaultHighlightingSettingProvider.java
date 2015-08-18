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

package com.goide.highlighting;

import com.goide.GoFileType;
import com.goide.psi.GoFile;
import com.goide.util.GoUtil;
import com.intellij.codeInsight.daemon.impl.analysis.DefaultHighlightingSettingProvider;
import com.intellij.codeInsight.daemon.impl.analysis.FileHighlightingSetting;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.FileIndexFacade;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GoDefaultHighlightingSettingProvider extends DefaultHighlightingSettingProvider {
  @Nullable
  @Override
  public FileHighlightingSetting getDefaultSetting(@NotNull Project project, @NotNull VirtualFile file) {
    if (file.getFileType() != GoFileType.INSTANCE) return null;
    if (project.isDefault()) return null;
    if (!file.isValid()) return null;
    if (FileIndexFacade.getInstance(project).isInContent(file)) return null;

    PsiFile psiFile = PsiManager.getInstance(project).findFile(file);
    if (psiFile instanceof GoFile && !GoUtil.importPathToIgnore(((GoFile)psiFile).getImportPath())) {
      return FileHighlightingSetting.SKIP_HIGHLIGHTING;
    }
    return null;
  }
}
