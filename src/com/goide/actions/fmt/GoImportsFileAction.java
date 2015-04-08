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

package com.goide.actions.fmt;

import com.goide.GoConstants;
import com.goide.GoEnvironmentUtil;
import com.goide.util.GoExecutor;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.PathEnvironmentVariableUtil;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public class GoImportsFileAction extends GoExternalToolsAction {
  @Override
  protected boolean doSomething(@NotNull VirtualFile virtualFile, @NotNull Project project, @NotNull String title)
    throws ExecutionException {
    File executable = getExecutable();
    if (executable == null) {
      Notifications.Bus.notify(GoConstants.GO_EXECUTION_NOTIFICATION_GROUP.createNotification(title,
                                                "Looks like you haven't any goimports executable in your PATH",
                                                NotificationType.WARNING, null), project);
      return false;
    }
    return super.doSomething(virtualFile, project, title);
  }

  @Nullable
  @Override
  protected GoExecutor createExecutor(Module module, @NotNull String title, @NotNull String filePath) {
    File executable = getExecutable();
    assert executable != null;
    return GoExecutor.in(module).withExePath(executable.getAbsolutePath()).withParameters("-w", filePath).showOutputOnError();
  }

  @Nullable
  private static File getExecutable() {
    return PathEnvironmentVariableUtil.findInPath(GoEnvironmentUtil.getBinaryFileNameForPath("goimports"));
  }
}
