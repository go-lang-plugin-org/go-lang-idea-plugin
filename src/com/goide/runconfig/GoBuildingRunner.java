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

package com.goide.runconfig;

import com.goide.runconfig.application.GoApplicationConfiguration;
import com.goide.runconfig.application.GoApplicationRunningState;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionResult;
import com.intellij.execution.RunProfileStarter;
import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.execution.configurations.RunProfile;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.executors.DefaultRunExecutor;
import com.intellij.execution.process.ProcessAdapter;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.execution.runners.AsyncGenericProgramRunner;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.RunContentBuilder;
import com.intellij.execution.ui.RunContentDescriptor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.util.SystemInfoRt;
import com.intellij.openapi.util.io.FileUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.concurrency.AsyncPromise;
import org.jetbrains.concurrency.Promise;

import java.io.File;
import java.io.IOException;

public class GoBuildingRunner extends AsyncGenericProgramRunner {
  private static final String ID = "GoBuildingRunner";

  @NotNull
  @Override
  public String getRunnerId() {
    return ID;
  }

  @Override
  public boolean canRun(@NotNull String executorId, @NotNull RunProfile profile) {
    return DefaultRunExecutor.EXECUTOR_ID.equals(executorId) && profile instanceof GoApplicationConfiguration;
  }

  @NotNull
  @Override
  protected Promise<RunProfileStarter> prepare(@NotNull ExecutionEnvironment environment, @NotNull RunProfileState state)
    throws ExecutionException {
    final File outputFile;
    String saveFilePath = ((GoApplicationRunningState)state).myConfiguration.getOutputFilePath();
    try {
      RunnerAndConfigurationSettings settings = environment.getRunnerAndConfigurationSettings();
      if (saveFilePath.isEmpty()) {
        outputFile = FileUtil.createTempFile(settings != null ? settings.getName() : "application", "go", true);
      } else if (new File(saveFilePath).isDirectory()) {
        String fileName = settings != null ? settings.getName() : "goApplication";
        if (SystemInfoRt.isWindows) {
          fileName += ".exe";
        }
        outputFile = new File(saveFilePath + File.separator + fileName);

        if (!outputFile.exists()) {
          if (!outputFile.createNewFile()) {
            throw new ExecutionException("Could not create output file " + outputFile.getAbsolutePath());
          }
        }
      } else if (new File(saveFilePath).isFile()) {
        outputFile = new File(saveFilePath);

        if (!outputFile.exists()) {
          if (!outputFile.createNewFile()) {
            throw new ExecutionException("Could not create output file " + outputFile.getAbsolutePath());
          }
        }
      } else {
        throw new ExecutionException("Could not create the output file in a reasonable manner, please check your configuration");
      }
      if (!outputFile.setExecutable(true)) {
        throw new ExecutionException("Can't make temporary file executable (" + outputFile.getAbsolutePath());
      }
    }
    catch (IOException e) {
      throw new ExecutionException("Can't create temporary output file", e);
    }

    final AsyncPromise<RunProfileStarter> promise = new AsyncPromise<RunProfileStarter>();
    FileDocumentManager.getInstance().saveAllDocuments();

    ((GoApplicationRunningState)state).createCommonExecutor()
      .withParameters("build")
      .withParameterString(((GoApplicationRunningState)state).getGoBuildParams())
      .withParameters("-o", outputFile.getAbsolutePath(), ((GoApplicationRunningState)state).getTarget())
      .showNotifications(true)
      .showOutputOnError()
      .withPresentableName("go build")
      .withProcessListener(new ProcessAdapter() {
        @Override
        public void processTerminated(ProcessEvent event) {
          super.processTerminated(event);
          if (event.getExitCode() == 0) {
            promise.setResult(new MyStarter(outputFile.getAbsolutePath()));
          }
          else {
            promise.setResult(null);
          }
        }
      }).executeWithProgress(false);

    return promise;
  }

  private class MyStarter extends RunProfileStarter {
    private final String tmpFilePath;

    private MyStarter(@NotNull String tmpFilePath) {
      this.tmpFilePath = tmpFilePath;
    }

    @Nullable
    @Override
    public RunContentDescriptor execute(@NotNull RunProfileState state, @NotNull ExecutionEnvironment env) throws ExecutionException {
      if (state instanceof GoApplicationRunningState) {
        FileDocumentManager.getInstance().saveAllDocuments();
        ((GoApplicationRunningState)state).setTmpFilePath(tmpFilePath);
        ExecutionResult executionResult = state.execute(env.getExecutor(), GoBuildingRunner.this);
        return executionResult != null ? new RunContentBuilder(executionResult, env).showRunContent(env.getContentToReuse()) : null;
      }
      return null;
    }
  }
}
