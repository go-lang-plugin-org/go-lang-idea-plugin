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

package com.goide.actions;

import com.goide.sdk.GoSdkType;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ProjectRootManager;

public class NewGoGroup extends DefaultActionGroup {
  @Override
  public void update(AnActionEvent e)
  {
    super.update(e);

    if (e.getProject() == null) {
      return;
    }

    Sdk sdk = ProjectRootManager.getInstance(e.getProject()).getProjectSdk();

    final Module data = LangDataKeys.MODULE.getData(e.getDataContext());
    e.getPresentation().setVisible(data != null &&
                                   sdk != null &&
                                   sdk.getSdkType() instanceof GoSdkType);
  }
}
