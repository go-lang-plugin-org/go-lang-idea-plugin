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

package com.goide.quickfix;

import com.goide.project.GoModuleSettings;
import com.goide.sdk.GoSdkService;
import com.intellij.codeInspection.LocalQuickFixBase;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.util.ThreeState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.goide.project.GoVendoringUtil.isVendoringEnabled;
import static com.goide.project.GoVendoringUtil.vendoringCanBeDisabled;

public class GoDisableVendoringInModuleQuickFix extends LocalQuickFixBase {
  @NotNull private final Module myModule;

  private GoDisableVendoringInModuleQuickFix(@NotNull Module module) {
    super("Disable vendoring experiment support in module '" + module.getName() + "'", "Disable vendoring experiment support in module");
    myModule = module;
  }

  @Override
  public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
    if (!myModule.isDisposed()) {
      GoModuleSettings.getInstance(myModule).setVendoringEnabled(ThreeState.NO);
    }
  }

  public static GoDisableVendoringInModuleQuickFix create(@Nullable Module module) {
    if (!isVendoringEnabled(module)) {
      return null;
    }
    String version = GoSdkService.getInstance(module.getProject()).getSdkVersion(module);
    return vendoringCanBeDisabled(version) ? new GoDisableVendoringInModuleQuickFix(module) : null;
  }
}
