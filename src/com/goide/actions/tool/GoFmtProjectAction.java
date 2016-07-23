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

package com.goide.actions.tool;

import com.goide.sdk.GoSdkService;
import com.goide.sdk.GoSdkUtil;
import com.goide.util.GoExecutor;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GoFmtProjectAction extends DumbAwareAction {
  @Override
  public void update(@NotNull AnActionEvent e) {
    Project project = e.getProject();
    e.getPresentation().setEnabled(project != null && GoSdkService.getInstance(project).getSdkHomePath(null) != null);
  }

  @Override
  public void actionPerformed(@NotNull AnActionEvent e) {
    Project project = e.getProject();
    assert project != null;

    FileDocumentManager.getInstance().saveAllDocuments();
    for (Module module : GoSdkUtil.getGoModules(project)) {
      for (VirtualFile file : ModuleRootManager.getInstance(module).getContentRoots()) {
        fmt(project, module, "go fmt " + file.getPath(), file);
      }
    }
  }

  private static void fmt(@NotNull Project project, @Nullable Module module, @NotNull String presentation, @NotNull VirtualFile dir) {
    GoExecutor.in(project, module).withPresentableName(presentation).withWorkDirectory(dir.getPath())
      .withParameters("fmt", "./...").showOutputOnError().executeWithProgress(false,
                                                                              result -> VfsUtil.markDirtyAndRefresh(true, true, true, dir));
  }
}
