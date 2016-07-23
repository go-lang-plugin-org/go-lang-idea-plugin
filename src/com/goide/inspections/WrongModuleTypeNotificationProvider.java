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
import com.goide.GoModuleType;
import com.goide.sdk.GoSdkService;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.EditorNotificationPanel;
import com.intellij.ui.EditorNotifications;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class WrongModuleTypeNotificationProvider extends EditorNotifications.Provider<EditorNotificationPanel> implements DumbAware {
  private static final Key<EditorNotificationPanel> KEY = Key.create("Wrong module type");
  private static final String DONT_ASK_TO_CHANGE_MODULE_TYPE_KEY = "do.not.ask.to.change.module.type";

  private final Project myProject;

  public WrongModuleTypeNotificationProvider(@NotNull Project project) {
    myProject = project;
  }

  @NotNull
  @Override
  public Key<EditorNotificationPanel> getKey() {
    return KEY;
  }

  @Override
  public EditorNotificationPanel createNotificationPanel(@NotNull VirtualFile file, @NotNull FileEditor fileEditor) {
    if (file.getFileType() != GoFileType.INSTANCE) return null;
    Module module = ModuleUtilCore.findModuleForFile(file, myProject);
    return module == null || GoSdkService.getInstance(myProject).isGoModule(module) || getIgnoredModules(myProject).contains(module.getName()) 
           ? null
           : createPanel(myProject, module);
  }

  @NotNull
  private static EditorNotificationPanel createPanel(@NotNull Project project, @NotNull Module module) {
    EditorNotificationPanel panel = new EditorNotificationPanel();
    panel.setText("'" + module.getName() + "' is not Go Module, some code insight might not work here");
    panel.createActionLabel("Change module type to Go and reload project", () -> {
      int message = Messages.showOkCancelDialog(project, "Updating module type requires project reload. Proceed?", "Update Module Type",
                                                "Reload project", "Cancel", null);
      if (message == Messages.YES) {
        module.setOption(Module.ELEMENT_TYPE, GoModuleType.getInstance().getId());
        project.save();
        EditorNotifications.getInstance(project).updateAllNotifications();
        ProjectManager.getInstance().reloadProject(project);
      }
    });
    panel.createActionLabel("Don't show again for this module", () -> {
      Set<String> ignoredModules = getIgnoredModules(project);
      ignoredModules.add(module.getName());
      PropertiesComponent.getInstance(project).setValue(DONT_ASK_TO_CHANGE_MODULE_TYPE_KEY, StringUtil.join(ignoredModules, ","));
      EditorNotifications.getInstance(project).updateAllNotifications();
    });
    return panel;
  }

  @NotNull
  private static Set<String> getIgnoredModules(@NotNull Project project) {
    String value = PropertiesComponent.getInstance(project).getValue(DONT_ASK_TO_CHANGE_MODULE_TYPE_KEY, "");
    return ContainerUtil.newLinkedHashSet(StringUtil.split(value, ","));
  }
}