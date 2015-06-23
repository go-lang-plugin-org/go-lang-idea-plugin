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

package com.goide.runconfig.testing.frameworks.gotest;

import com.goide.runconfig.testing.GoTestRunConfigurationBase;
import com.goide.runconfig.testing.GoTestRunningState;
import com.goide.runconfig.testing.ui.GoTestRunConfigurationEditorForm;
import com.intellij.execution.configurations.ConfigurationType;
import com.intellij.execution.configurations.ModuleBasedConfiguration;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.testframework.TestConsoleProperties;
import com.intellij.execution.testframework.sm.runner.OutputToGeneralTestEventsConverter;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public class GotestRunConfiguration extends GoTestRunConfigurationBase {
  public GotestRunConfiguration(@NotNull Project project, String name, @NotNull ConfigurationType configurationType) {
    super(project, name, configurationType);
  }

  @Override
  public OutputToGeneralTestEventsConverter createTestEventsConverter(@NotNull TestConsoleProperties consoleProperties) {
    return new GotestEventsConverter(consoleProperties);
  }

  @Override
  public String getFrameworkName() {
    return "gotest";
  }

  @NotNull
  @Override
  public SettingsEditor<? extends RunConfiguration> getConfigurationEditor() {
    return new GoTestRunConfigurationEditorForm(getProject());
  }

  @NotNull
  @Override
  protected GoTestRunningState newRunningState(@NotNull ExecutionEnvironment env, @NotNull Module module) {
    return new GoTestRunningState(env, module, this);
  }

  @NotNull
  @Override
  protected ModuleBasedConfiguration createInstance() {
    return new GotestRunConfiguration(getProject(), getName(), GotestRunConfigurationType.getInstance());
  }
}
