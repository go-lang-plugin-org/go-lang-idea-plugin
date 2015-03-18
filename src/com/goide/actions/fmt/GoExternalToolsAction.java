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

import com.goide.psi.GoFile;
import com.intellij.execution.ExecutionException;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.util.ExceptionUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class GoExternalToolsAction extends AnAction implements DumbAware {
  private static final Logger LOG = Logger.getInstance(GoExternalToolsAction.class);

  private void error(@NotNull PsiFile file, @NotNull Project project, @NotNull String groupId, @Nullable Exception ex) {
    Notifications.Bus.notify(new Notification(groupId,
                                              getErrorTitle(file.getName()),
                                              ex == null ? "" : ExceptionUtil.getUserStackTrace(ex, LOG),
                                              NotificationType.ERROR), project);
  }

  protected void warning(@NotNull Project project, @NotNull String groupId, @NotNull String content) {
    Notifications.Bus.notify(new Notification(groupId, getWarningTitle(), content, NotificationType.WARNING), project);
  }

  @NotNull
  protected String getWarningTitle() {
    return "";
  }

  @NotNull
  protected String getErrorTitle(@NotNull String fileName) {
    return "";
  }

  @Override
  public void update(@NotNull AnActionEvent e) {
    e.getPresentation().setEnabled(e.getProject() != null && e.getData(CommonDataKeys.PSI_FILE) instanceof GoFile);
  }

  @Override
  public void actionPerformed(@NotNull AnActionEvent e) {
    PsiFile file = e.getData(CommonDataKeys.PSI_FILE);
    Project project = e.getProject();
    assert project != null;
    assert file instanceof GoFile;
    VirtualFile vFile = file.getVirtualFile();

    String groupId = e.getPresentation().getText();

    try {
      doSomething(file, project, vFile, StringUtil.notNullize(groupId, ""));
    }
    catch (Exception ex) {
      error(file, project, groupId, ex);
      LOG.error(ex);
    }
  }

  protected abstract boolean doSomething(@NotNull PsiFile file,
                                         @NotNull Project project,
                                         @Nullable VirtualFile virtualFile,
                                         @NotNull String groupId) throws ExecutionException;
}
