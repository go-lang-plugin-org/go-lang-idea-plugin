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
import com.goide.project.GoModuleSettings;
import com.goide.util.GoUtil;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.vfs.newvfs.BulkFileListener;
import com.intellij.openapi.vfs.newvfs.events.VFileEvent;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.impl.source.tree.injected.InjectedLanguageUtil;
import com.intellij.ui.EditorNotificationPanel;
import com.intellij.ui.EditorNotifications;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.messages.MessageBusConnection;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;

public class GoFileIgnoredByBuildToolNotificationProvider extends EditorNotifications.Provider<EditorNotificationPanel>
  implements DumbAware {

  private static final Key<EditorNotificationPanel> KEY = Key.create("Go file ignored by build tool");
  private static final String DO_NOT_SHOW_NOTIFICATION_ABOUT_IGNORE_BY_BUILD_TOOL = "DO_NOT_SHOW_NOTIFICATION_ABOUT_IGNORE_BY_BUILD_TOOL";

  private final Project myProject;

  public GoFileIgnoredByBuildToolNotificationProvider(@NotNull Project project,
                                                      @NotNull EditorNotifications notifications,
                                                      @NotNull FileEditorManager fileEditorManager) {
    myProject = project;
    MessageBusConnection connection = myProject.getMessageBus().connect(myProject);
    connection.subscribe(GoModuleSettings.TOPIC, module -> notifications.updateAllNotifications());
    connection.subscribe(VirtualFileManager.VFS_CHANGES, new BulkFileListener.Adapter() {
      @Override
      public void after(@NotNull List<? extends VFileEvent> events) {
        if (!myProject.isDisposed()) {
          Set<VirtualFile> openFiles = ContainerUtil.newHashSet(fileEditorManager.getSelectedFiles());
          for (VFileEvent event : events) {
            VirtualFile file = event.getFile();
            if (file != null && openFiles.contains(file)) {
              notifications.updateNotifications(file);
            }
          }
        }
      }
    });
  }

  @NotNull
  @Override
  public Key<EditorNotificationPanel> getKey() {
    return KEY;
  }

  @Override
  public EditorNotificationPanel createNotificationPanel(@NotNull VirtualFile file, @NotNull FileEditor fileEditor) {
    if (file.getFileType() == GoFileType.INSTANCE) {
      PsiFile psiFile = PsiManager.getInstance(myProject).findFile(file);
      if (InjectedLanguageUtil.findInjectionHost(psiFile) != null) {
        return null;
      }
      Module module = psiFile != null ? ModuleUtilCore.findModuleForPsiElement(psiFile) : null;
      if (GoUtil.fileToIgnore(file.getName())) {
        if (!PropertiesComponent.getInstance().getBoolean(DO_NOT_SHOW_NOTIFICATION_ABOUT_IGNORE_BY_BUILD_TOOL, false)) {
          return createIgnoredByBuildToolPanel(myProject, file);
        }
      }
      else if (module != null && !GoUtil.matchedForModuleBuildTarget(psiFile, module)) {
        return createMismatchedTargetPanel(module, file);
      }
    }
    return null;
  }

  private static EditorNotificationPanel createIgnoredByBuildToolPanel(@NotNull Project project, @NotNull VirtualFile file) {
    EditorNotificationPanel panel = new EditorNotificationPanel();
    String fileName = file.getName();
    panel.setText("'" + fileName + "' will be ignored by build tool since its name starts with '" + fileName.charAt(0) + "'");
    panel.createActionLabel("Do not show again", () -> {
      PropertiesComponent.getInstance().setValue(DO_NOT_SHOW_NOTIFICATION_ABOUT_IGNORE_BY_BUILD_TOOL, true);
      EditorNotifications.getInstance(project).updateAllNotifications();
    });
    return panel;
  }

  @NotNull
  private static EditorNotificationPanel createMismatchedTargetPanel(@NotNull Module module, @NotNull VirtualFile file) {
    EditorNotificationPanel panel = new EditorNotificationPanel();
    panel.setText("'" + file.getName() + "' doesn't match to target system. File will be ignored by build tool");
    panel.createActionLabel("Edit Go project settings", () -> GoModuleSettings.showModulesConfigurable(module));
    return panel;
  }
}