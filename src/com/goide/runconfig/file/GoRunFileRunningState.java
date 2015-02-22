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

package com.goide.runconfig.file;

import com.goide.GoConstants;
import com.goide.GoEnvironmentUtil;
import com.goide.runconfig.GoRunningState;
import com.goide.sdk.GoSdkUtil;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.configurations.ParametersList;
import com.intellij.execution.filters.TextConsoleBuilder;
import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.module.Module;
import org.jetbrains.annotations.NotNull;

public class GoRunFileRunningState extends GoRunningState {
  private final GoRunFileConfiguration myConfiguration;

  public GoRunFileRunningState(@NotNull ExecutionEnvironment env, @NotNull Module module, GoRunFileConfiguration configuration) {
    super(env, module);
    myConfiguration = configuration;
  }

  @NotNull
  @Override
  protected GeneralCommandLine getCommand(String sdkHomePath) throws ExecutionException {
    GeneralCommandLine commandLine = new GeneralCommandLine();
    String executable = GoEnvironmentUtil.getExecutableForSdk(sdkHomePath).getAbsolutePath();
    commandLine.getEnvironment().put(GoConstants.GO_PATH, GoSdkUtil.retrieveGoPath(myModule));
    commandLine.setExePath(executable);
    ParametersList list = commandLine.getParametersList();
    list.add("run");
    String filePath = myConfiguration.getFilePath();
    list.addParametersString(filePath);
    list.addParametersString(myConfiguration.getParams());
    commandLine.withWorkDirectory(myConfiguration.getWorkingDirectory());
    TextConsoleBuilder consoleBuilder = TextConsoleBuilderFactory.getInstance().createBuilder(myModule.getProject());
    setConsoleBuilder(consoleBuilder);
    return commandLine;
  }
}
