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
import com.intellij.ui.EditorNotificationPanel;
import com.intellij.ui.EditorNotifications;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.messages.MessageBusConnection;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;

public class MismatchedBuildTargetNotificationProvider extends EditorNotifications.Provider<EditorNotificationPanel> implements DumbAware {
  private static final Key<EditorNotificationPanel> KEY = Key.create("Mismatched build target");

  private final Project myProject;

  public MismatchedBuildTargetNotificationProvider(@NotNull Project project, 
                                                   @NotNull final EditorNotifications notifications,
                                                   @NotNull final FileEditorManager fileEditorManager) {
    myProject = project;
    MessageBusConnection connection = myProject.getMessageBus().connect(myProject);
    connection.subscribe(GoModuleSettings.TOPIC, new GoModuleSettings.BuildTargetListener() {
      @Override
      public void changed(@NotNull Module module) {
        notifications.updateAllNotifications();
      }
    });
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
      if (psiFile != null && !GoUtil.allowed(psiFile)) {
        Module module = ModuleUtilCore.findModuleForPsiElement(psiFile);
        if (module != null) {
          return createPanel(module, file);
        }
      }
    }
    return null;
  }

  @NotNull
  private static EditorNotificationPanel createPanel(@NotNull final Module module, @NotNull VirtualFile file) {
    EditorNotificationPanel panel = new EditorNotificationPanel();
    panel.setText("'" + file.getName() + "' doesn't match to target system");
    panel.createActionLabel("Open Go project settings", new Runnable() {
      @Override
      public void run() {
        GoModuleSettings.showModulesConfigurable(module);
      }
    });
    return panel;
  }
}