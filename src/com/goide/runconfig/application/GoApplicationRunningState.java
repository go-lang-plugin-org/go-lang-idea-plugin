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

import com.goide.runconfig.GoRunningState;
import com.goide.util.GoExecutor;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.process.ProcessOutput;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.util.io.FileUtil;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

public class GoApplicationRunningState extends GoRunningState<GoApplicationConfiguration> {
  private File myTempFile;

  public GoApplicationRunningState(@NotNull ExecutionEnvironment env, @NotNull Module module,
                                   @NotNull GoApplicationConfiguration configuration) {
    super(env, module, configuration);
  }

  @NotNull
  @Override
  protected ProcessHandler startProcess() throws ExecutionException {
    setConsoleBuilder(TextConsoleBuilderFactory.getInstance().createBuilder(myModule.getProject()));
    try {
      myTempFile = FileUtil.createTempFile(myConfiguration.getName(), "go", true);
      //noinspection ResultOfMethodCallIgnored
      myTempFile.setExecutable(true);
    }
    catch (IOException e) {
      throw new ExecutionException("Can't create temporary output file", e);
    }
    
    final ProcessOutput processOutput = new ProcessOutput();
    final Ref<ExecutionException> buildException = Ref.create();
    final Ref<Boolean> success = Ref.create(false);
    
    try {
      ProgressManager.getInstance().run(new Task.Modal(myModule.getProject(), "go build", true) {
        public void run(@NotNull ProgressIndicator indicator) {
          if (myProject == null || myProject.isDisposed()) {
            return;
          }
          try {
            success.set(GoExecutor.in(myModule)
                          .addParameters("build", "-o", myTempFile.getAbsolutePath(), myConfiguration.getFilePath())
                          .withProcessOutput(processOutput)
                          .showOutputOnError()
                          .execute());
          }
          catch (ExecutionException e) {
            buildException.set(e);
          }
        }
      });

      if (!buildException.isNull()) {
        throw buildException.get();
      }

      if (!success.get()) {
        throw new ExecutionException("Build failure. `go build` is finished with exit code " + processOutput.getExitCode());
      }

      return super.startProcess();
    }
    finally {
      //noinspection ResultOfMethodCallIgnored
      myTempFile.delete();
    }
  }


  @Override
  protected GoExecutor patchExecutor(@NotNull GoExecutor executor) throws ExecutionException {
    return executor.withExePath(myTempFile.getAbsolutePath());
  }
}
