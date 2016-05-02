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

package com.goide.runconfig.testing.frameworks.gobench;

import com.goide.psi.GoFunctionDeclaration;
import com.goide.psi.GoFunctionOrMethodDeclaration;
import com.goide.runconfig.testing.GoTestFinder;
import com.goide.runconfig.testing.GoTestFramework;
import com.goide.runconfig.testing.GoTestRunConfiguration;
import com.goide.runconfig.testing.GoTestRunningState;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.testframework.TestConsoleProperties;
import com.intellij.execution.testframework.sm.runner.OutputToGeneralTestEventsConverter;
import com.intellij.openapi.module.Module;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GobenchFramework extends GoTestFramework {
  public static final String NAME = "gobench";
  public static final GobenchFramework INSTANCE = new GobenchFramework();

  @NotNull
  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public boolean isAvailable(@Nullable Module module) {
    return true;
  }

  @Override
  public boolean isAvailableOnFile(@Nullable PsiFile file) {
    return GoTestFinder.isTestFile(file);
  }

  @Override
  public boolean isAvailableOnFunction(@Nullable GoFunctionOrMethodDeclaration functionOrMethodDeclaration) {
    return functionOrMethodDeclaration instanceof GoFunctionDeclaration && GoTestFinder.isBenchmarkFunction(functionOrMethodDeclaration);
  }

  @NotNull
  @Override
  protected GoTestRunningState newRunningState(@NotNull ExecutionEnvironment env,
                                               @NotNull Module module,
                                               @NotNull GoTestRunConfiguration runConfiguration) {
    return new GobenchRunningState(env, module, runConfiguration);
  }

  @NotNull
  @Override
  public OutputToGeneralTestEventsConverter createTestEventsConverter(@NotNull TestConsoleProperties consoleProperties) {
    return new GobenchEventsConverter(consoleProperties);
  }
}
