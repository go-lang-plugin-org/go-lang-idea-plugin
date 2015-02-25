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

import com.goide.GoEnvironmentUtil;
import com.goide.sdk.GoSdkService;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.KillableColoredProcessHandler;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.process.ProcessAdapter;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.util.ExceptionUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public class GoFmtProjectAction extends AnAction implements DumbAware {
  private static final String NOTIFICATION_TITLE = "Reformat code with go gmt";
  private static final Logger LOG = Logger.getInstance(GoFmtProjectAction.class);

  @Override
  public void update(@NotNull AnActionEvent e) {
    e.getPresentation().setEnabled(e.getProject() != null);
  }

  @Override
  public void actionPerformed(@NotNull AnActionEvent e) {
    final Project project = e.getProject();
    assert project != null;

    final String groupId = e.getPresentation().getText();
    try {
      GeneralCommandLine commandLine = new GeneralCommandLine();
      String sdkHome = GoSdkService.getInstance(project).getSdkHomePath(null);
      if (StringUtil.isEmpty(sdkHome)) {
        warning(project, groupId, "Project sdk is not valid");
        return;
      }

      FileDocumentManager.getInstance().saveAllDocuments();

      File executable = GoEnvironmentUtil.getExecutableForSdk(sdkHome);

      commandLine.withWorkDirectory(project.getBasePath());
      commandLine.setExePath(executable.getAbsolutePath());
      commandLine.addParameters("fmt", "./...");

      final String commandLineString = commandLine.getCommandLineString();
      OSProcessHandler handler = new KillableColoredProcessHandler(commandLine.createProcess(), commandLineString);
      handler.addProcessListener(new ProcessAdapter() {
        @Override
        public void processTerminated(ProcessEvent event) {
          ApplicationManager.getApplication().invokeLater(new Runnable() {
            @Override
            public void run() {
              ApplicationManager.getApplication().runWriteAction(new Runnable() {
                @Override
                public void run() {
                  VirtualFileManager.getInstance().syncRefresh();
                }
              });
            }
          });
        }
        });
      handler.startNotify();
    }
    catch (Exception ex) {
      error(project, groupId, ex);
      LOG.error(ex);
    }
  }

  private static void error(@NotNull Project project, @NotNull String groupId, @Nullable Exception ex) {
    Notifications.Bus.notify(new Notification(groupId, "Project formatting with go fmt failed",
                                              ex == null ? "" : ExceptionUtil.getUserStackTrace(ex, LOG),
                                              NotificationType.ERROR), project);
  }

  private static void warning(@NotNull Project project, @NotNull String groupId, @NotNull String content) {
    Notifications.Bus.notify(new Notification(groupId, NOTIFICATION_TITLE, content, NotificationType.WARNING), project);
  }
}
