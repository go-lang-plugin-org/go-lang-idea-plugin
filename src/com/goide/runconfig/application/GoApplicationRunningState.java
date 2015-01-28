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

package com.goide.runconfig.application;

import com.goide.jps.model.JpsGoSdkType;
import com.goide.runconfig.GoRunningState;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.filters.TextConsoleBuilder;
import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.compiler.CompilerPaths;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.util.PathUtil;
import org.jetbrains.annotations.NotNull;

public class GoApplicationRunningState extends GoRunningState {
  private final GoApplicationConfiguration myConfiguration;

  public GoApplicationRunningState(@NotNull ExecutionEnvironment env, @NotNull Module module, GoApplicationConfiguration configuration) {
    super(env, module);
    myConfiguration = configuration;
  }

  @NotNull
  @Override
  protected GeneralCommandLine getCommand(@NotNull Sdk sdk) throws ExecutionException {
    GeneralCommandLine commandLine = new GeneralCommandLine();
    String homePath = sdk.getHomePath();
    assert homePath != null;
    String outputDirectory = CompilerPaths.getModuleOutputPath(myModule, false);
    assert outputDirectory != null;
    String modulePath = PathUtil.getParentPath(myModule.getModuleFilePath());
    String executable = FileUtil.toSystemDependentName(JpsGoSdkType.getBinaryPathByModulePath(modulePath, outputDirectory));
    commandLine.setExePath(executable);
    commandLine.getParametersList().addParametersString(myConfiguration.getParams());
    commandLine.withWorkDirectory(PathUtil.getParentPath(executable));
    TextConsoleBuilder consoleBuilder = TextConsoleBuilderFactory.getInstance().createBuilder(myModule.getProject());
    setConsoleBuilder(consoleBuilder);
    return commandLine;
  }
}
