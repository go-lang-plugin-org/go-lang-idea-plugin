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

package com.goide.runconfig.testing;

import com.goide.runconfig.testing.frameworks.gocheck.GocheckFramework;
import com.goide.runconfig.testing.frameworks.gotest.GotestFramework;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.testframework.TestConsoleProperties;
import com.intellij.execution.testframework.sm.runner.OutputToGeneralTestEventsConverter;
import com.intellij.openapi.module.Module;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class GoTestFramework {
  @NotNull
  public static GoTestFramework fromName(@Nullable String name) {
    if (GocheckFramework.NAME.equals(name)) {
      return GocheckFramework.INSTANCE;
    }
    return GotestFramework.INSTANCE;
  }
  
  @NotNull
  public abstract String getName();

  public abstract boolean isAvailable(@Nullable Module module);

  @NotNull
  protected abstract GoTestRunningState newRunningState(@NotNull ExecutionEnvironment env,
                                                        @NotNull Module module, @NotNull GoTestRunConfiguration runConfiguration);

  @NotNull
  public abstract OutputToGeneralTestEventsConverter createTestEventsConverter(@NotNull TestConsoleProperties consoleProperties);
}
