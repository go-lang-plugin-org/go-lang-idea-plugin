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

package com.goide.runconfig.application;

import com.goide.runconfig.GoRunningState;
import com.goide.util.GoExecutor;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.process.ProcessAdapter;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.util.text.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class GoApplicationRunningState extends GoRunningState<GoApplicationConfiguration> {
  private String myOutputFilePath;

  public GoApplicationRunningState(@NotNull ExecutionEnvironment env, @NotNull Module module,
                                   @NotNull GoApplicationConfiguration configuration) {
    super(env, module, configuration);
  }

  @NotNull
  public String getTarget() {
    return myConfiguration.getKind() == GoApplicationConfiguration.Kind.PACKAGE
           ? myConfiguration.getPackage()
           : myConfiguration.getFilePath();
  }

  @NotNull
  public String getGoBuildParams() {
    return myConfiguration.getGoToolParams();
  }

  @NotNull
  @Override
  protected ProcessHandler startProcess() throws ExecutionException {
    ProcessHandler processHandler = super.startProcess();
    processHandler.addProcessListener(new ProcessAdapter() {
      @Override
      public void processTerminated(ProcessEvent event) {
        super.processTerminated(event);
        if (StringUtil.isEmpty(myConfiguration.getOutputFilePath())) {
          File file = new File(myOutputFilePath);
          if (file.exists()) {
            //noinspection ResultOfMethodCallIgnored
            file.delete();
          }
        }
      }
    });
    return processHandler;
  }

  @Override
  protected GoExecutor patchExecutor(@NotNull GoExecutor executor) throws ExecutionException {
    return executor.withExePath(myOutputFilePath);
  }

  public void setOutputFilePath(@NotNull String outputFilePath) {
    myOutputFilePath = outputFilePath;
  }
}
