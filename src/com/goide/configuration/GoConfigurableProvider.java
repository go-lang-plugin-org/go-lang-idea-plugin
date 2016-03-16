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

import com.goide.GoConstants;
import com.goide.codeInsight.imports.GoAutoImportConfigurable;
import com.goide.sdk.GoSdkService;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurableProvider;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.options.UnnamedConfigurable;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GoConfigurableProvider extends ConfigurableProvider {
  @NotNull private final Project myProject;

  public GoConfigurableProvider(@NotNull Project project) {
    myProject = project;
  }

  @Nullable
  @Override
  public Configurable createConfigurable() {
    Configurable projectSettingsConfigurable = new GoProjectSettingsConfigurable(myProject);
    Configurable librariesConfigurable = new GoLibrariesConfigurableProvider(myProject).createConfigurable();
    Configurable sdkConfigurable = GoSdkService.getInstance(myProject).createSdkConfigurable();
    Configurable autoImportConfigurable = new GoAutoImportConfigurable(myProject, false);
    return sdkConfigurable != null
           ? new GoCompositeConfigurable(sdkConfigurable, projectSettingsConfigurable, librariesConfigurable, autoImportConfigurable)
           : new GoCompositeConfigurable(projectSettingsConfigurable, librariesConfigurable, autoImportConfigurable);
  }

  private static class GoCompositeConfigurable extends SearchableConfigurable.Parent.Abstract {
    private Configurable[] myConfigurables;

    public GoCompositeConfigurable(Configurable... configurables) {
      myConfigurables = configurables;
    }

    @Override
    protected Configurable[] buildConfigurables() {
      return myConfigurables;
    }

    @NotNull
    @Override
    public String getId() {
      return "go";
    }

    @Nls
    @Override
    public String getDisplayName() {
      return GoConstants.GO;
    }

    @Nullable
    @Override
    public String getHelpTopic() {
      return null;
    }

    @Override
    public void disposeUIResources() {
      super.disposeUIResources();
      myConfigurables = null;
    }
  }

  public static class GoProjectSettingsConfigurable extends GoModuleAwareConfigurable {
    public GoProjectSettingsConfigurable(@NotNull Project project) {
      super(project, "Project Settings", null);
    }

    @NotNull
    @Override
    protected UnnamedConfigurable createModuleConfigurable(Module module) {
      return new GoModuleSettingsConfigurable(module, false);
    }
  }
}
