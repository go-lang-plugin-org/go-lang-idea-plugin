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

package com.goide.sdk;

import com.goide.GoModuleType;
import com.intellij.ProjectTopics;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ComponentManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.*;
import com.intellij.openapi.roots.ui.configuration.ProjectSettingsService;
import com.intellij.psi.util.CachedValueProvider;
import com.intellij.psi.util.CachedValuesManager;
import com.intellij.util.ObjectUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GoIdeaSdkService extends GoSdkService {
  public GoIdeaSdkService(@NotNull Project project) {
    super(project);
    myProject.getMessageBus().connect(project).subscribe(ProjectTopics.PROJECT_ROOTS, new ModuleRootAdapter() {
      @Override
      public void rootsChanged(ModuleRootEvent event) {
        incModificationCount();
      }
    });
  }

  @Override
  public String getSdkHomePath(@Nullable Module module) {
    ComponentManager holder = ObjectUtils.notNull(module, myProject);
    return CachedValuesManager.getManager(myProject).getCachedValue(holder, () -> {
      Sdk sdk = getGoSdk(module);
      return CachedValueProvider.Result.create(sdk != null ? sdk.getHomePath() : null, this);
    });
  }

  @Nullable
  @Override
  public String getSdkVersion(@Nullable Module module) {
    String parentVersion = super.getSdkVersion(module);
    if (parentVersion != null) {
      return parentVersion;
    }

    ComponentManager holder = ObjectUtils.notNull(module, myProject);
    return CachedValuesManager.getManager(myProject).getCachedValue(holder, () -> {
      Sdk sdk = getGoSdk(module);
      return CachedValueProvider.Result.create(sdk != null ? sdk.getVersionString() : null, this);
    });
  }

  @Override
  public void chooseAndSetSdk(@Nullable Module module) {
    Sdk projectSdk = ProjectSettingsService.getInstance(myProject).chooseAndSetSdk();
    if (projectSdk == null && module != null) {
      ApplicationManager.getApplication().runWriteAction(() -> {
        if (!module.isDisposed()) {
          ModuleRootModificationUtil.setSdkInherited(module);
        }
      });
    }
  }

  @Override
  public boolean isGoModule(@Nullable Module module) {
    return super.isGoModule(module) && ModuleUtil.getModuleType(module) == GoModuleType.getInstance();
  }

  private Sdk getGoSdk(@Nullable Module module) {
    if (module != null) {
      Sdk sdk = ModuleRootManager.getInstance(module).getSdk();
      if (sdk != null && sdk.getSdkType() instanceof GoSdkType) {
        return sdk;
      }
    }
    Sdk sdk = ProjectRootManager.getInstance(myProject).getProjectSdk();
    return sdk != null && sdk.getSdkType() instanceof GoSdkType ? sdk : null;
  }
}
