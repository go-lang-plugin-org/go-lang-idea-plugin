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

package com.goide.ui.pm;

import com.goide.GoModuleType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.WindowWrapper;
import com.intellij.openapi.ui.WindowWrapperBuilder;

import java.util.Collection;

public class ManagerAction extends AnAction {

  @Override
  public void actionPerformed(AnActionEvent e) {
    Project project = e.getProject();
    if (project == null) {
      return;
    }
    Collection<Module> modules = ModuleUtil.getModulesOfType(project, GoModuleType.getInstance());
    if (modules.size() == 0) {
      return;
    }

    WindowWrapperBuilder window = new WindowWrapperBuilder(WindowWrapper.Mode.MODAL, Manager.getInstance().getComponent());
    WindowWrapper myWindow = window.build();
    myWindow.setTitle("go get package manager");
    Manager.setWindow(myWindow);
    Manager.setProject(project);
    Manager.setModule(modules.iterator().next());
    myWindow.show();
    myWindow.getWindow().transferFocus();
  }
}
