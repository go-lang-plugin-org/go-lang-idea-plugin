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

import com.goide.GoEnvironmentUtil;
import com.goide.runconfig.GoRunningState;
import com.intellij.execution.DefaultExecutionResult;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionResult;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.filters.TextConsoleBuilder;
import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.ProgramRunner;
import com.intellij.execution.testframework.autotest.ToggleAutoTestAction;
import com.intellij.execution.testframework.sm.SMTestRunnerConnectionUtil;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFileManager;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class GoTestRunningState extends GoRunningState {
  private final GoTestRunConfiguration myConfiguration;

  @NotNull
  @Override
  public ExecutionResult execute(@NotNull Executor executor, @NotNull ProgramRunner runner) throws ExecutionException {
    ProcessHandler processHandler = startProcess();
    TextConsoleBuilder consoleBuilder = TextConsoleBuilderFactory.getInstance().createBuilder(myModule.getProject());
    setConsoleBuilder(consoleBuilder);

    GoTestConsoleProperties consoleProperties = new GoTestConsoleProperties(myConfiguration, executor);
    ConsoleView consoleView = SMTestRunnerConnectionUtil.createAndAttachConsole("Go", processHandler, consoleProperties, getEnvironment());
    consoleView.addMessageFilter(new GoTestConsoleFilter(myModule, myConfiguration.getWorkingDirectory()));

    DefaultExecutionResult executionResult = new DefaultExecutionResult(consoleView, processHandler);
    executionResult.setRestartActions(new ToggleAutoTestAction(getEnvironment()));
    return executionResult;
  }

  public GoTestRunningState(@NotNull ExecutionEnvironment env, @NotNull Module module, GoTestRunConfiguration configuration) {
    super(env, module);
    myConfiguration = configuration;
  }

  @NotNull
  @Override
  protected GeneralCommandLine getCommand(String sdkHomePath) throws ExecutionException {
    String executable = GoEnvironmentUtil.getExecutableForSdk(sdkHomePath).getAbsolutePath();

    GeneralCommandLine installDependencies = new GeneralCommandLine();
    installDependencies.setExePath(executable);
    installDependencies.addParameter("test");
    installDependencies.addParameter("-i");
    fillCommandLineWithParameters(installDependencies);
    try {
      installDependencies.createProcess().waitFor();
      VirtualFileManager.getInstance().syncRefresh();
    }
    catch (InterruptedException ignore) {
      Thread.currentThread().interrupt();
    }

    GeneralCommandLine runTests = new GeneralCommandLine();
    runTests.setExePath(executable);
    runTests.addParameter("test");
    runTests.addParameter("-v");
    fillCommandLineWithParameters(runTests);
    runTests.getParametersList().addParametersString(myConfiguration.getParams());
    return runTests;
  }

  private void fillCommandLineWithParameters(@NotNull GeneralCommandLine commandLine) {
    commandLine.withWorkDirectory(myConfiguration.getWorkingDirectory());
    switch (myConfiguration.getKind()) {
      case DIRECTORY:
        String relativePath = FileUtil.getRelativePath(myConfiguration.getWorkingDirectory(),
                                                       myConfiguration.getDirectoryPath(),
                                                       File.separatorChar);
        if (relativePath != null) {
          if (".".equals(relativePath)) {
            commandLine.addParameter("./...");
          }
          else {
            commandLine.addParameter("./" + relativePath);
          }
        }
        else {
          commandLine.addParameter("./...");
          commandLine.withWorkDirectory(myConfiguration.getDirectoryPath());
        }
        break;
      case PACKAGE:
        commandLine.addParameter(myConfiguration.getPackage());
        break;
      case FILE:
        commandLine.addParameter(myConfiguration.getFilePath());
        break;
    }
    String pattern = myConfiguration.getPattern();
    if (!StringUtil.isEmpty(pattern)) {
      commandLine.addParameter("--run='" + pattern + "'");
    }
  }
}
