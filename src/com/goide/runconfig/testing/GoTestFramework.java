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

import com.goide.psi.GoFunctionOrMethodDeclaration;
import com.goide.runconfig.testing.frameworks.gobench.GobenchFramework;
import com.goide.runconfig.testing.frameworks.gocheck.GocheckFramework;
import com.goide.runconfig.testing.frameworks.gotest.GotestFramework;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.testframework.TestConsoleProperties;
import com.intellij.execution.testframework.sm.runner.OutputToGeneralTestEventsConverter;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.module.Module;
import com.intellij.psi.PsiFile;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public abstract class GoTestFramework {
  private static class Lazy {

    private static final ArrayList<GoTestFramework> ALL_FRAMEWORKS = ContainerUtil.newArrayList(
      GotestFramework.INSTANCE, GocheckFramework.INSTANCE, GobenchFramework.INSTANCE
    );
  }
  public static List<GoTestFramework> all() {
    return Lazy.ALL_FRAMEWORKS;
  }

  public Collection<? extends AnAction> getGenerateMethodActions() {
    return Collections.emptyList();
  }

  @NotNull
  public static GoTestFramework fromName(@Nullable String name) {
    for (GoTestFramework framework : all()) {
      if (framework.getName().equals(name)) {
        return framework;
      }
    }
    return GotestFramework.INSTANCE;
  }

  @NotNull
  public abstract String getName();

  public abstract boolean isAvailable(@Nullable Module module);

  public abstract boolean isAvailableOnFile(@Nullable PsiFile file);

  public abstract boolean isAvailableOnFunction(@Nullable GoFunctionOrMethodDeclaration functionOrMethodDeclaration);

  @NotNull
  protected abstract GoTestRunningState newRunningState(@NotNull ExecutionEnvironment env,
                                                        @NotNull Module module, @NotNull GoTestRunConfiguration runConfiguration);

  @NotNull
  public abstract OutputToGeneralTestEventsConverter createTestEventsConverter(@NotNull TestConsoleProperties consoleProperties);
}
