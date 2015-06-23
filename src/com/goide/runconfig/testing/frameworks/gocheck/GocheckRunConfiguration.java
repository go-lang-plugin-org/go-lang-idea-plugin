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
import com.goide.runconfig.GoModuleBasedConfiguration;
import com.goide.runconfig.testing.GoTestRunConfigurationBase;
import com.goide.runconfig.testing.GoTestRunningState;
import com.goide.runconfig.testing.ui.GoTestRunConfigurationEditorForm;
import com.goide.stubs.index.GoPackagesIndex;
import com.goide.util.GoUtil;
import com.intellij.execution.configurations.ConfigurationType;
import com.intellij.execution.configurations.ModuleBasedConfiguration;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.configurations.RuntimeConfigurationException;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.testframework.TestConsoleProperties;
import com.intellij.execution.testframework.sm.runner.OutputToGeneralTestEventsConverter;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.stubs.StubIndex;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

public class GocheckRunConfiguration extends GoTestRunConfigurationBase {
  private static final Pattern GO_CHECK_IMPORT_PATH = Pattern.compile("gopkg\\.in/check\\.v\\d+");
  private static final Pattern GO_CHECK_GITHUB_IMPORT_PATH = Pattern.compile("github\\.com/go-check/check\\.v\\d+");

  public GocheckRunConfiguration(@NotNull Project project, String name, @NotNull ConfigurationType configurationType) {
    super(project, name, configurationType);
  }

  @Override
  public OutputToGeneralTestEventsConverter createTestEventsConverter(@NotNull TestConsoleProperties consoleProperties) {
    return new GocheckEventsConverter(consoleProperties);
  }

  @Override
  public String getFrameworkName() {
    return "gocheck";
  }

  @Override
  public void checkConfiguration() throws RuntimeConfigurationException {
    super.checkConfiguration();
    GoModuleBasedConfiguration configurationModule = getConfigurationModule();
    Module module = configurationModule.getModule();
    assert module != null;
    for (GoFile file : StubIndex.getElements(GoPackagesIndex.KEY, "check", getProject(), GoUtil.moduleScope(module), GoFile.class)) {
      String importPath = file.getImportPath();
      if (importPath != null && (GO_CHECK_IMPORT_PATH.matcher(importPath).matches() ||
                                 GO_CHECK_GITHUB_IMPORT_PATH.matcher(importPath).matches())) {
        return;
      }
    }
    throw new RuntimeConfigurationException("Cannot find gocheck package in GOPATH");
  }

  @NotNull
  @Override
  public SettingsEditor<? extends RunConfiguration> getConfigurationEditor() {
    return new GoTestRunConfigurationEditorForm(getProject());
  }

  @NotNull
  @Override
  protected GoTestRunningState newRunningState(@NotNull ExecutionEnvironment env, @NotNull Module module) {
    return new GocheckRunningState(env, module, this);
  }

  @NotNull
  @Override
  protected ModuleBasedConfiguration createInstance() {
    return new GocheckRunConfiguration(getProject(), getName(), GocheckRunConfigurationType.getInstance());
  }
}
