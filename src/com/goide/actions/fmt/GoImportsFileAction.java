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

package com.goide.actions.fmt;

import com.goide.GoConstants;
import com.goide.codeInsight.imports.GoGetPackageFix;
import com.goide.sdk.GoSdkUtil;
import com.goide.util.GoExecutor;
import com.intellij.execution.ExecutionException;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationListener;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.event.HyperlinkEvent;

public class GoImportsFileAction extends GoExternalToolsAction {
  private static final String GO_GET_IMPORTS = "goGetImports";

  @Override
  protected boolean doSomething(@NotNull VirtualFile virtualFile, @Nullable Module module, @NotNull Project project, @NotNull String title)
    throws ExecutionException {
    VirtualFile executable = getExecutable(project, module);
    if (executable == null) {
      String message = "Can't find `goimports` in GOPATH. Try to invoke <a href=\"" + GO_GET_IMPORTS + "\">go get goimports</a>";
      NotificationListener listener = new MyNotificationListener(project, module);
      Notifications.Bus.notify(GoConstants.GO_NOTIFICATION_GROUP.createNotification(title, message, NotificationType.WARNING, listener),
                               project);
      return false;
    }
    return super.doSomething(virtualFile, module, project, title);
  }

  @NotNull
  @Override
  protected GoExecutor createExecutor(@NotNull Project project, @Nullable Module module, @NotNull String title, @NotNull String filePath) {
    VirtualFile executable = getExecutable(project, module);
    assert executable != null;
    return GoExecutor.in(project, module).withExePath(executable.getPath()).withParameters("-w", filePath).showOutputOnError();
  }

  @Nullable
  private static VirtualFile getExecutable(@NotNull Project project, @Nullable Module module) {
    return GoSdkUtil.findExecutableInGoPath("goimports", project, module);
  }

  private static class MyNotificationListener implements NotificationListener {
    private final Project myProject;
    private final Module myModule;

    private MyNotificationListener(@NotNull Project project, @Nullable Module module) {
      myProject = project;
      myModule = module;
    }

    @Override
    public void hyperlinkUpdate(@NotNull Notification notification, @NotNull HyperlinkEvent event) {
      if (event.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
        final String description = event.getDescription();
        if (GO_GET_IMPORTS.equals(description)) {
          GoGetPackageFix.applyFix(myProject, myModule, "golang.org/x/tools/cmd/goimports", false);
          notification.expire();
        }
      }
    }
  }
}
