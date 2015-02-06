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

package com.goide.runconfig;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.CommandLineState;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.util.text.StringUtil;
import org.jetbrains.annotations.NotNull;

public abstract class GoRunningState extends CommandLineState {
  @NotNull protected final Module myModule;

  public GoRunningState(@NotNull ExecutionEnvironment env, @NotNull Module module) {
    super(env);
    myModule = module;
  }

  @NotNull
  @Override
  protected ProcessHandler startProcess() throws ExecutionException {
    Sdk sdk = ModuleRootManager.getInstance(myModule).getSdk();
    if (sdk == null) {
      throw new ExecutionException("Sdk is not set for module " + myModule.getName());
    }
    
    final String sdkHomePath = sdk.getHomePath();
    if (StringUtil.isEmpty(sdkHomePath)) {
      throw new ExecutionException("Sdk home path is empty for module " + myModule.getName());
    }
    
    GeneralCommandLine commandLine = getCommand(sdkHomePath);
    return new OSProcessHandler(commandLine.createProcess(), commandLine.getCommandLineString());
  }

  @NotNull
  protected abstract GeneralCommandLine getCommand(String sdkHomePath) throws ExecutionException;
}
