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

package com.goide.inspections;

import com.goide.GoFileType;
import com.goide.GoLanguage;
import com.goide.project.GoLibrariesService;
import com.goide.project.GoModuleLibrariesInitializer;
import com.goide.sdk.GoSdkService;
import com.goide.sdk.GoSdkUtil;
import com.intellij.ProjectTopics;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectBundle;
import com.intellij.openapi.roots.ModuleRootAdapter;
import com.intellij.openapi.roots.ModuleRootEvent;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.ui.EditorNotificationPanel;
import com.intellij.ui.EditorNotifications;
import com.intellij.util.messages.MessageBusConnection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class WrongSdkConfigurationNotificationProvider extends EditorNotifications.Provider<EditorNotificationPanel> implements DumbAware {
  private static final Key<EditorNotificationPanel> KEY = Key.create("Setup Go SDK");

  private final Project myProject;

  public WrongSdkConfigurationNotificationProvider(@NotNull Project project, @NotNull final EditorNotifications notifications) {
    myProject = project;
    MessageBusConnection connection = myProject.getMessageBus().connect(project);
    connection.subscribe(ProjectTopics.PROJECT_ROOTS, new ModuleRootAdapter() {
      @Override
      public void rootsChanged(ModuleRootEvent event) {
        notifications.updateAllNotifications();
      }
    });
    connection.subscribe(GoLibrariesService.LIBRARIES_TOPIC, new GoLibrariesService.LibrariesListener() {
      @Override
      public void librariesChanged(@NotNull Collection<String> newRootUrls) {
        notifications.updateAllNotifications();
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
    if (file.getFileType() != GoFileType.INSTANCE) return null;

    PsiFile psiFile = PsiManager.getInstance(myProject).findFile(file);
    if (psiFile == null) return null;

    if (psiFile.getLanguage() != GoLanguage.INSTANCE) return null;

    Module module = ModuleUtilCore.findModuleForPsiElement(psiFile);
    if (module == null) return null;

    String sdkHomePath = GoSdkService.getInstance(myProject).getSdkHomePath(module);
    if (StringUtil.isEmpty(sdkHomePath)) {
      return createMissingSdkPanel(myProject, module);
    }

    String goPath = GoSdkUtil.retrieveGoPath(myProject, module);
    if (StringUtil.isEmpty(goPath.trim())) {
      return createEmptyGoPathPanel(myProject);
    }

    return null;
  }

  @NotNull
  private static EditorNotificationPanel createMissingSdkPanel(@NotNull final Project project, @Nullable final Module module) {
    EditorNotificationPanel panel = new EditorNotificationPanel();
    panel.setText(ProjectBundle.message("project.sdk.not.defined"));
    panel.createActionLabel(ProjectBundle.message("project.sdk.setup"), new Runnable() {
      @Override
      public void run() {
        GoSdkService.getInstance(project).chooseAndSetSdk(module);
      }
    });
    return panel;
  }

  @NotNull
  private static EditorNotificationPanel createEmptyGoPathPanel(@NotNull final Project project) {
    EditorNotificationPanel panel = new EditorNotificationPanel();
    panel.setText("GOPATH is empty");
    panel.createActionLabel("Configure Go Libraries", new Runnable() {
      @Override
      public void run() {
        GoModuleLibrariesInitializer.showModulesConfigurable(project);
      }
    });
    return panel;
  }
}