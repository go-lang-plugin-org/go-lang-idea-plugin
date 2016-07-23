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
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.PsiElement;
import com.intellij.util.Consumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GoGetPackageFix extends LocalQuickFixBase implements HighPriorityAction {
  @NotNull private final String myPackage;

  public GoGetPackageFix(@NotNull String packageName) {
    super("go get -t " + packageName + "/...", "go get");
    myPackage = packageName;
  }

  public static void applyFix(@NotNull Project project,
                              @Nullable Module module,
                              @NotNull String packageName,
                              boolean startInBackground) {
    String sdkPath = GoSdkService.getInstance(project).getSdkHomePath(module);
    if (StringUtil.isEmpty(sdkPath)) return;
    CommandProcessor.getInstance().runUndoTransparentAction(() -> {
      Consumer<Boolean> consumer = aBoolean -> VirtualFileManager.getInstance().asyncRefresh(null);
      GoExecutor.in(project, module).withPresentableName("go get -t " + packageName + "/...")
        .withParameters("get", "-t", packageName+"/...").showNotifications(false, true).showOutputOnError()
        .executeWithProgress(!startInBackground, consumer);
    });
  }

  @Override
  public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
    PsiElement element = descriptor.getPsiElement();
    if (element != null) {
      applyFix(project, ModuleUtilCore.findModuleForPsiElement(element.getContainingFile()), myPackage, true);
    }
  }
}
