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

package com.goide.runconfig.testing;

import com.goide.runconfig.testing.frameworks.gocheck.GocheckEventsConverter;
import com.goide.runconfig.testing.frameworks.gocheck.GocheckRunConfigurationType;
import com.goide.runconfig.testing.frameworks.gotest.GotestEventsConverter;
import com.goide.runconfig.testing.frameworks.gotest.GotestRunConfigurationType;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.testframework.TestConsoleProperties;
import com.intellij.execution.testframework.sm.SMCustomMessagesParsing;
import com.intellij.execution.testframework.sm.runner.OutputToGeneralTestEventsConverter;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.util.config.Storage;
import org.jetbrains.annotations.NotNull;

public class GoTestConsoleProperties extends TestConsoleProperties implements SMCustomMessagesParsing {

  private final RunConfiguration myConfiguration;

  public GoTestConsoleProperties(@NotNull GoTestRunConfigurationBase configuration, @NotNull Executor executor) {
    super(new Storage.PropertiesComponentStorage("GoTestSupport.", PropertiesComponent.getInstance()), configuration.getProject(),
          executor);
    myConfiguration = configuration;
  }

  @NotNull
  @Override
  public OutputToGeneralTestEventsConverter createTestEventsConverter(@NotNull String testFrameworkName,
                                                                      @NotNull TestConsoleProperties consoleProperties) {
    if (myConfiguration.getType() == GotestRunConfigurationType.getInstance()) {
      return new GotestEventsConverter(consoleProperties);
    }
    if (myConfiguration.getType() == GocheckRunConfigurationType.getInstance()) {
      return new GocheckEventsConverter(consoleProperties);
    }
    throw new RuntimeException("Unexpected test framework: " + testFrameworkName);
  }

  @Override
  public RunConfiguration getConfiguration() {
    return myConfiguration;
  }
}
