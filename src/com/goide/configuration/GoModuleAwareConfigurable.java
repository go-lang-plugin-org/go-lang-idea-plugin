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

package com.goide.configuration;

import com.goide.sdk.GoSdkService;
import com.intellij.application.options.ModuleAwareProjectConfigurable;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public abstract class GoModuleAwareConfigurable extends ModuleAwareProjectConfigurable {
  public GoModuleAwareConfigurable(@NotNull Project project, String displayName, String helpTopic) {
    super(project, displayName, helpTopic);
  }

  @Override
  protected boolean isSuitableForModule(@NotNull Module module) {
    if (module.isDisposed()) {
      return false;
    }
    Project project = module.getProject();
    return !project.isDisposed() && GoSdkService.getInstance(project).isGoModule(module);
  }
}
