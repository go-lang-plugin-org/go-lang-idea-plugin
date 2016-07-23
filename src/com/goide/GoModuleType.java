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

package com.goide;

import com.goide.sdk.GoSdkType;
import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ide.util.projectWizard.ProjectJdkForModuleStep;
import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.module.ModuleTypeManager;
import com.intellij.openapi.roots.ui.configuration.ModulesProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class GoModuleType extends ModuleType<GoModuleBuilder> {
  public GoModuleType() {
    super(GoConstants.MODULE_TYPE_ID);
  }

  @NotNull
  public static GoModuleType getInstance() {
    return (GoModuleType)ModuleTypeManager.getInstance().findByID(GoConstants.MODULE_TYPE_ID);
  }

  @NotNull
  @Override
  public GoModuleBuilder createModuleBuilder() {
    return new GoModuleBuilder();
  }

  @NotNull
  @Override
  public String getName() {
    return "Go Module";
  }

  @NotNull
  @Override
  public String getDescription() {
    return "Go modules are used for developing <b>Go</b> applications.";
  }

  @Nullable
  @Override
  public Icon getBigIcon() {
    return GoIcons.MODULE_ICON;
  }

  @Nullable
  @Override
  public Icon getNodeIcon(boolean isOpened) {
    return GoIcons.ICON;
  }

  @NotNull
  @Override
  public ModuleWizardStep[] createWizardSteps(@NotNull WizardContext wizardContext,
                                              @NotNull GoModuleBuilder moduleBuilder,
                                              @NotNull ModulesProvider modulesProvider) {
    return new ModuleWizardStep[]{new ProjectJdkForModuleStep(wizardContext, GoSdkType.getInstance()) {
      @Override
      public void updateDataModel() {
        super.updateDataModel();
        moduleBuilder.setModuleJdk(getJdk());
      }
    }};
  }
}
