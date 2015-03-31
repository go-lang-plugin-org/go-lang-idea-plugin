/*
 * Copyright 2013-2014 Sergey Ignatov, Alexander Zolotov
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

package com.goide.codeInsight.imports;

import com.goide.sdk.GoSdkService;
import com.goide.util.GoExecutor;
import com.intellij.codeInsight.intention.HighPriorityAction;
import com.intellij.codeInspection.LocalQuickFixBase;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

public class GoGetPackageFix extends LocalQuickFixBase implements HighPriorityAction {
  @NotNull private final String myPackage;

  public GoGetPackageFix(@NotNull String packageName) {
    super("Go get '" + packageName + "'");
    myPackage = packageName;
  }

  @Override
  public void applyFix(@NotNull final Project project, @NotNull ProblemDescriptor descriptor) {
    PsiElement element = descriptor.getPsiElement();
    final Module module = ModuleUtilCore.findModuleForPsiElement(element);
    if (module == null) {
      return;
    }

    applyFix(project, module, myPackage, true);
  }

  public static void applyFix(@NotNull final Project project,
                              @NotNull final Module module,
                              final String packageName,
                              final boolean startInBackground) {
    final String sdkPath = GoSdkService.getInstance(project).getSdkHomePath(module);
    if (StringUtil.isEmpty(sdkPath)) {
      return;
    }
    CommandProcessor.getInstance().runUndoTransparentAction(new Runnable() {
      @Override
      public void run() {
        GoExecutor.in(module).withPresentableName("go get " + packageName)
          .withParameters("get", packageName).showNotifications(false).showOutputOnError().executeWithProgress(!startInBackground);
      }
    });
  }
}
