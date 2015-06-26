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

import com.intellij.execution.Executor;
import com.intellij.execution.configurations.RunProfile;
import com.intellij.execution.testframework.TestConsoleProperties;
import com.intellij.execution.testframework.sm.SMCustomMessagesParsing;
import com.intellij.execution.testframework.sm.runner.OutputToGeneralTestEventsConverter;
import com.intellij.execution.testframework.sm.runner.SMTRunnerConsoleProperties;
import org.jetbrains.annotations.NotNull;

public class GoTestConsoleProperties extends SMTRunnerConsoleProperties implements SMCustomMessagesParsing {
  public GoTestConsoleProperties(@NotNull GoTestRunConfiguration configuration, @NotNull Executor executor) {
    super(configuration, configuration.getTestFramework().getName(), executor);
  }

  @NotNull
  @Override
  public OutputToGeneralTestEventsConverter createTestEventsConverter(@NotNull String testFrameworkName,
                                                                      @NotNull TestConsoleProperties consoleProperties) {
    RunProfile configuration = getConfiguration();
    assert configuration instanceof GoTestRunConfiguration;
    return ((GoTestRunConfiguration)configuration).createTestEventsConverter(consoleProperties);
  }
}
