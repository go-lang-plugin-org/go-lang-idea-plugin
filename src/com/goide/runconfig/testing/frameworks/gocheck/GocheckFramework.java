/*
 * Copyright 2013-2015 Sergey Ignatov, Alexander Zolotov, Mihai Toader, Florin Patan
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

package com.goide.runconfig.testing.frameworks.gocheck;

import com.goide.psi.GoFile;
import com.goide.psi.GoMethodDeclaration;
import com.goide.psi.impl.GoPsiImplUtil;
import com.goide.runconfig.testing.GoTestFinder;
import com.goide.runconfig.testing.GoTestFramework;
import com.goide.runconfig.testing.GoTestRunConfiguration;
import com.goide.runconfig.testing.GoTestRunningState;
import com.goide.stubs.index.GoPackagesIndex;
import com.goide.util.GoUtil;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.testframework.TestConsoleProperties;
import com.intellij.execution.testframework.sm.runner.OutputToGeneralTestEventsConverter;
import com.intellij.openapi.module.Module;
import com.intellij.psi.stubs.StubIndex;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Pattern;

public class GocheckFramework extends GoTestFramework {
  public static final String NAME = "gocheck";
  public static final GocheckFramework INSTANCE = new GocheckFramework();
  private static final Pattern GO_CHECK_IMPORT_PATH = Pattern.compile("gopkg\\.in/check\\.v\\d+");
  private static final Pattern GO_CHECK_GITHUB_IMPORT_PATH = Pattern.compile("github\\.com/go-check/check\\.v\\d+");

  private GocheckFramework() {
  }

  @Nullable
  public static String getGocheckTestName(@NotNull GoMethodDeclaration method) {
    String methodName = GoTestFinder.getTestFunctionName(method);
    if (methodName != null) {
      String suiteName = GoPsiImplUtil.getText(method.getReceiver().getType());
      if (!suiteName.isEmpty()) {
        return suiteName + "." + methodName;
      }
    }
    return null;
  }

  @NotNull
  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public boolean isAvailable(@Nullable Module module) {
    if (module == null) return false;
    for (GoFile file : StubIndex.getElements(GoPackagesIndex.KEY, "check", module.getProject(), GoUtil.moduleScope(module), GoFile.class)) {
      String importPath = file.getImportPath();
      if (importPath != null) {
        return GO_CHECK_IMPORT_PATH.matcher(importPath).matches() || GO_CHECK_GITHUB_IMPORT_PATH.matcher(importPath).matches();
      }
    }
    return false;
  }

  @NotNull
  @Override
  protected GoTestRunningState newRunningState(@NotNull ExecutionEnvironment env,
                                               @NotNull Module module,
                                               @NotNull GoTestRunConfiguration runConfiguration) {
    return new GocheckRunningState(env, module, runConfiguration);
  }

  @NotNull
  @Override
  public OutputToGeneralTestEventsConverter createTestEventsConverter(@NotNull TestConsoleProperties consoleProperties) {
    return new GocheckEventsConverter(consoleProperties);
  }
}
