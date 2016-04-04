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

package com.goide.runconfig;

import com.goide.GoConstants;
import com.goide.util.GoExecutor;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.CommandLineState;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.KillableColoredProcessHandler;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.process.ProcessOutputTypes;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.module.Module;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public abstract class GoRunningState<T extends GoRunConfigurationBase<?>> extends CommandLineState {
  @NotNull protected final Module myModule;

  @NotNull
  public T getConfiguration() {
    return myConfiguration;
  }

  @NotNull protected final T myConfiguration;

  public GoRunningState(@NotNull ExecutionEnvironment env, @NotNull Module module, @NotNull T configuration) {
    super(env);
    myModule = module;
    myConfiguration = configuration;
    addConsoleFilters(new GoConsoleFilter(myConfiguration.getProject(), myModule, myConfiguration.getWorkingDirectoryUrl()));
  }

  @NotNull
  @Override
  protected ProcessHandler startProcess() throws ExecutionException {
    GoExecutor executor = patchExecutor(createCommonExecutor());
    GeneralCommandLine commandLine = executor.withParameterString(myConfiguration.getParams()).createCommandLine();
    return new KillableColoredProcessHandler(commandLine) {
      @Override
      public void startNotify() {
        if (isShowGoPathAndGoRootValuesOnStart()) {
          Map<String, String> environment = commandLine.getEnvironment();
          notifyTextAvailable("GOROOT=" + environment.getOrDefault(GoConstants.GO_ROOT, "") + '\n', ProcessOutputTypes.SYSTEM);
          notifyTextAvailable("GOPATH=" + environment.getOrDefault(GoConstants.GO_PATH, "") + '\n', ProcessOutputTypes.SYSTEM);
        }
        super.startNotify();
      }
    };
  }

  @NotNull
  public GoExecutor createCommonExecutor() {
    return GoExecutor.in(myModule).withWorkDirectory(myConfiguration.getWorkingDirectory())
      .withExtraEnvironment(myConfiguration.getCustomEnvironment())
      .withPassParentEnvironment(myConfiguration.isPassParentEnvironment());
  }

  protected GoExecutor patchExecutor(@NotNull GoExecutor executor) throws ExecutionException {
    return executor;
  }
  
  protected boolean isShowGoPathAndGoRootValuesOnStart() {
    return true;
  }
}
