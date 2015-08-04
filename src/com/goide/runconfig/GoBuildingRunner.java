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

import com.goide.GoEnvironmentUtil;
import com.goide.dlv.DlvDebugProcess;
import com.goide.dlv.DlvRemoteVmConnection;
import com.goide.runconfig.application.GoApplicationConfiguration;
import com.goide.runconfig.application.GoApplicationRunningState;
import com.goide.util.GoHistoryProcessListener;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionResult;
import com.intellij.execution.RunProfileStarter;
import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.execution.configurations.RunProfile;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.executors.DefaultDebugExecutor;
import com.intellij.execution.executors.DefaultRunExecutor;
import com.intellij.execution.process.ProcessAdapter;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.execution.runners.AsyncGenericProgramRunner;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.RunContentBuilder;
import com.intellij.execution.ui.RunContentDescriptor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.util.ArrayUtil;
import com.intellij.util.net.NetUtils;
import com.intellij.xdebugger.XDebugProcess;
import com.intellij.xdebugger.XDebugProcessStarter;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.XDebuggerManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.concurrency.AsyncFunction;
import org.jetbrains.concurrency.AsyncPromise;
import org.jetbrains.concurrency.Promise;
import org.jetbrains.debugger.connection.RemoteVmConnection;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;

public class GoBuildingRunner extends AsyncGenericProgramRunner {
  private static final String ID = "GoBuildingRunner";
  @Nullable private ExecutionResult myExecutionResult;

  @NotNull
  @Override
  public String getRunnerId() {
    return ID;
  }

  @Override
  public boolean canRun(@NotNull String executorId, @NotNull RunProfile profile) {
    if (profile instanceof GoApplicationConfiguration) {
      return DefaultRunExecutor.EXECUTOR_ID.equals(executorId) || DefaultDebugExecutor.EXECUTOR_ID.equals(executorId);
    }
    return false;
  }

  @NotNull
  @Override
  protected Promise<RunProfileStarter> prepare(@NotNull final ExecutionEnvironment environment, @NotNull final RunProfileState state)
    throws ExecutionException {
    final File outputFile;
    String outputDirectoryPath = ((GoApplicationRunningState)state).myConfiguration.getOutputFilePath();
    RunnerAndConfigurationSettings settings = environment.getRunnerAndConfigurationSettings();
    String configurationName = settings != null ? settings.getName() : "application";
    if (StringUtil.isEmpty(outputDirectoryPath)) {
      try {
        outputFile = FileUtil.createTempFile(configurationName, "go", true);
      }
      catch (IOException e) {
        throw new ExecutionException("Cannot create temporary output file", e);
      }
    }
    else {
      File outputDirectory = new File(outputDirectoryPath);
      if (outputDirectory.isDirectory() || !outputDirectory.exists() && outputDirectory.mkdirs()) {
        outputFile = new File(outputDirectoryPath, GoEnvironmentUtil.getBinaryFileNameForPath(configurationName));
        try {
          if (!outputFile.exists() && !outputFile.createNewFile()) {
            throw new ExecutionException("Cannot create output file " + outputFile.getAbsolutePath());
          }
        }
        catch (IOException e) {
          throw new ExecutionException("Cannot create output file " + outputFile.getAbsolutePath());
        }
      }
      else {
        throw new ExecutionException("Cannot create output file in " + outputDirectory.getAbsolutePath());
      }
    }
    if (!prepareFile(outputFile)) {
      throw new ExecutionException("Cannot make temporary file executable " + outputFile.getAbsolutePath());
    }
    
    FileDocumentManager.getInstance().saveAllDocuments();

    final AsyncPromise<RunProfileStarter> buildingPromise = new AsyncPromise<RunProfileStarter>();
    final GoHistoryProcessListener historyProcessListener = new GoHistoryProcessListener();
    ((GoApplicationRunningState)state).createCommonExecutor()
      .withParameters("build")
      .withParameterString(((GoApplicationRunningState)state).getGoBuildParams())
      .withParameters("-o", outputFile.getAbsolutePath())
      .withParameters(((GoApplicationRunningState)state).isDebug() ? new String[] {"-gcflags", "-N -l"} : ArrayUtil.EMPTY_STRING_ARRAY)
      .withParameters(((GoApplicationRunningState)state).getTarget())
      .showNotifications(true)
      .showOutputOnError()
      .disablePty()
      .withPresentableName("go build")
      .withProcessListener(historyProcessListener)
      .withProcessListener(new ProcessAdapter() {

        @Override
        public void processTerminated(ProcessEvent event) {
          super.processTerminated(event);
          if (event.getExitCode() == 0) {
            buildingPromise.setResult(new MyStarter(outputFile.getAbsolutePath(), historyProcessListener));
          }
          else {
            buildingPromise.setResult(null);
            buildingPromise.setError(new ExecutionException(event.getText()));
          }
        }
      }).executeWithProgress(false);


    return ((GoApplicationRunningState)state).isDebug() ? prepareDebugger(environment, state, buildingPromise) : buildingPromise;
  }

  private static boolean prepareFile(@NotNull File file) {
    try {
      FileUtil.writeToFile(file, new byte[]{0x7F, 'E', 'L', 'F'});
    }
    catch (IOException e) {
      return false;
    }
    return file.setExecutable(true);
  }

  @NotNull
  private Promise<RunProfileStarter> prepareDebugger(@NotNull final ExecutionEnvironment environment,
                                                     @NotNull final RunProfileState state,
                                                     AsyncPromise<RunProfileStarter> promise) {
    return promise.then(new AsyncFunction<RunProfileStarter, RunProfileStarter>() {
      @NotNull
      @Override
      public Promise<RunProfileStarter> fun(RunProfileStarter starter) {
        try {
          if (starter != null) {
            ((GoApplicationRunningState)state).setHistoryProcessHandler(((MyStarter)starter).myHistoryProcessListener);
            ((GoApplicationRunningState)state).setOutputFilePath(((MyStarter)starter).myOutputFilePath);
            myExecutionResult = state.execute(environment.getExecutor(), GoBuildingRunner.this);
          }
          return myExecutionResult != null ? Promise.resolve(starter) : Promise.<RunProfileStarter>reject("Cannot run debugger");
        }
        catch (ExecutionException e) {
          return Promise.reject(e);
        }
      }
    });
  }

  private class MyStarter extends RunProfileStarter {
    private final String myOutputFilePath;
    private final GoHistoryProcessListener myHistoryProcessListener;

    private MyStarter(@NotNull String outputFilePath, @NotNull GoHistoryProcessListener historyProcessListener) {
      myOutputFilePath = outputFilePath;
      myHistoryProcessListener = historyProcessListener;
    }

    @Nullable
    @Override
    public RunContentDescriptor execute(@NotNull final RunProfileState state, @NotNull final ExecutionEnvironment env) throws ExecutionException {
      if (state instanceof GoApplicationRunningState) {
        FileDocumentManager.getInstance().saveAllDocuments();
        ((GoApplicationRunningState)state).setHistoryProcessHandler(myHistoryProcessListener);
        ((GoApplicationRunningState)state).setOutputFilePath(myOutputFilePath);

        if (((GoApplicationRunningState)state).isDebug()) {
          return XDebuggerManager.getInstance(env.getProject()).startSession(env, new XDebugProcessStarter() {
            @NotNull
            @Override
            public XDebugProcess start(@NotNull XDebugSession session) throws ExecutionException {
              RemoteVmConnection connection = new DlvRemoteVmConnection();
              DlvDebugProcess process = new DlvDebugProcess(session, connection, myExecutionResult);
              connection.open(new InetSocketAddress(NetUtils.getLoopbackAddress(), 9090));
              return process;
            }
          }).getRunContentDescriptor();
        }
        else {
          ExecutionResult executionResult = state.execute(env.getExecutor(), GoBuildingRunner.this);
          return executionResult != null ? new RunContentBuilder(executionResult, env).showRunContent(env.getContentToReuse()) : null;
        }
      }
      return null;
    }
  }
}
