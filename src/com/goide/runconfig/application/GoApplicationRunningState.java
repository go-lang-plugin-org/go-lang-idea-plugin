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

package com.goide.runconfig.application;

import com.goide.GoConstants;
import com.goide.runconfig.GoRunningState;
import com.goide.util.GoExecutor;
import com.goide.util.GoHistoryProcessListener;
import com.goide.util.GoUtil;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.executors.DefaultDebugExecutor;
import com.intellij.execution.process.ProcessAdapter;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.openapi.util.text.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public class GoApplicationRunningState extends GoRunningState<GoApplicationConfiguration> {
  private String myOutputFilePath;
  @Nullable private GoHistoryProcessListener myHistoryProcessHandler;
  private int myDebugPort = 59090;
  private boolean myCompilationFailed;

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

  public boolean isDebug() {
    return DefaultDebugExecutor.EXECUTOR_ID.equals(getEnvironment().getExecutor().getId());
  }

  @NotNull
  @Override
  protected ProcessHandler startProcess() throws ExecutionException {
    ProcessHandler processHandler = myCompilationFailed ? new GoNopProcessHandler() : super.startProcess();
    processHandler.addProcessListener(new ProcessAdapter() {
      @Override
      public void startNotified(ProcessEvent event) {
        if (myHistoryProcessHandler != null) {
          myHistoryProcessHandler.apply(processHandler);
        }
      }

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
    if (isDebug()) {
      File dlv = dlv();
      if (dlv.exists() && !dlv.canExecute()) {
        //noinspection ResultOfMethodCallIgnored
        dlv.setExecutable(true, false);
      }
      return executor.withExePath(dlv.getAbsolutePath())
        .withParameters("--listen=localhost:" + myDebugPort, "--headless=true", "exec", myOutputFilePath, "--");
    }
    return executor.showGoEnvVariables(false).withExePath(myOutputFilePath);
  }

  @NotNull
  private static File dlv() {
    String dlvPath = System.getProperty("dlv.path");
    if (StringUtil.isNotEmpty(dlvPath)) return new File(dlvPath);
    return new File(GoUtil.getPlugin().getPath(),
                    "lib/dlv/" + (SystemInfo.isMac ? "mac" : SystemInfo.isWindows ? "windows" : "linux") + "/"
                    + GoConstants.DELVE_EXECUTABLE_NAME + (SystemInfo.isWindows ? ".exe" : ""));
  }

  public void setOutputFilePath(@NotNull String outputFilePath) {
    myOutputFilePath = outputFilePath;
  }

  public void setHistoryProcessHandler(@Nullable GoHistoryProcessListener historyProcessHandler) {
    myHistoryProcessHandler = historyProcessHandler;
  }

  public void setDebugPort(int debugPort) {
    myDebugPort = debugPort;
  }

  public void setCompilationFailed(boolean compilationFailed) {
    myCompilationFailed = compilationFailed;
  }
}
