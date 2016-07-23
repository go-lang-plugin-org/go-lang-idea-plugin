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

package com.goide.runconfig.testing;

import com.goide.GoTypes;
import com.goide.psi.GoFunctionDeclaration;
import com.goide.psi.GoFunctionOrMethodDeclaration;
import com.goide.psi.GoMethodDeclaration;
import com.goide.psi.GoReceiver;
import com.goide.runconfig.GoRunUtil;
import com.intellij.execution.TestStateStorage;
import com.intellij.execution.lineMarker.ExecutorAction;
import com.intellij.execution.lineMarker.RunLineMarkerContributor;
import com.intellij.execution.testframework.TestIconMapper;
import com.intellij.execution.testframework.sm.runner.states.TestStateInfo;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.Function;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class GoTestRunLineMarkerProvider extends RunLineMarkerContributor {
  private static final Function<PsiElement, String> TOOLTIP_PROVIDER = element -> "Run Test";

  @Nullable
  @Override
  public Info getInfo(PsiElement e) {
    if (e != null && e.getNode().getElementType() == GoTypes.IDENTIFIER) {
      PsiElement parent = e.getParent();
      PsiFile file = e.getContainingFile();
      if (!GoTestFinder.isTestFile(file)) {
        return null;
      }
      if (GoRunUtil.isPackageContext(e)) {
        return new Info(AllIcons.RunConfigurations.TestState.Run_run, TOOLTIP_PROVIDER, ExecutorAction.getActions(0));
      }
      else if (parent instanceof GoFunctionOrMethodDeclaration) {
        GoTestFunctionType functionType = GoTestFunctionType.fromName(((GoFunctionOrMethodDeclaration)parent).getName());
        if (functionType != null) {
          if (parent instanceof GoFunctionDeclaration) {
            return getInfo(GoTestLocator.PROTOCOL + "://" + ((GoFunctionDeclaration)parent).getName(), e.getProject());
          }
          else if (parent instanceof GoMethodDeclaration) {
            GoReceiver receiver = ((GoMethodDeclaration)parent).getReceiver();
            PsiElement receiverIdentifier = receiver != null ? receiver.getIdentifier() : null;
            String receiverText = receiverIdentifier != null ? receiverIdentifier.getText() + "." : "";
            return getInfo(GoTestLocator.PROTOCOL + "://" + receiverText + ((GoMethodDeclaration)parent).getName(), e.getProject());
          }
        }
      }
    }
    return null;
  }

  @NotNull
  private static Info getInfo(String url, Project project) {
    Icon icon = getTestStateIcon(url, project);
    return new Info(icon, TOOLTIP_PROVIDER, ExecutorAction.getActions(0));
  }

  private static Icon getTestStateIcon(@NotNull String url, @NotNull Project project) {
    TestStateStorage.Record state = TestStateStorage.getInstance(project).getState(url);
    if (state != null) {
      TestStateInfo.Magnitude magnitude = TestIconMapper.getMagnitude(state.magnitude);
      if (magnitude != null) {
        switch (magnitude) {
          case ERROR_INDEX:
          case FAILED_INDEX:
            return AllIcons.RunConfigurations.TestState.Red2;
          case PASSED_INDEX:
          case COMPLETE_INDEX:
            return AllIcons.RunConfigurations.TestState.Green2;
          default:
        }
      }
    }
    return AllIcons.RunConfigurations.TestState.Run;
  }
}
