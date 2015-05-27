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

package com.goide.debugger.ideagdb.run;

import com.goide.debugger.gdb.Gdb;
import com.goide.debugger.ideagdb.debug.GdbDebugProcess;
import com.goide.sdk.GoSdkService;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionResult;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.RunProfile;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.executors.DefaultDebugExecutor;
import com.intellij.execution.runners.DefaultProgramRunner;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.ui.RunContentDescriptor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.xdebugger.XDebugProcess;
import com.intellij.xdebugger.XDebugProcessStarter;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.XDebuggerManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GdbRunner extends DefaultProgramRunner {

  public static final String SET_AUTO_LOAD_SAFE_PATH = "set auto-load safe-path ";

  @NotNull
  @Override
  public String getRunnerId() {
    return "GdbRunner";
  }

  @Override
  public boolean canRun(@NotNull String executorId, @NotNull RunProfile profile) {
    return DefaultDebugExecutor.EXECUTOR_ID.equals(executorId) && profile instanceof GdbRunConfiguration;
  }

  @Override
  protected RunContentDescriptor doExecute(@NotNull RunProfileState state, @NotNull ExecutionEnvironment env) throws ExecutionException {
    FileDocumentManager.getInstance().saveAllDocuments();
    return createContentDescriptor(env.getProject(), env.getExecutor(), state, env);
  }

  @Nullable
  protected RunContentDescriptor createContentDescriptor(@NotNull Project project,
                                                         final Executor executor,
                                                         @NotNull final RunProfileState state,
                                                         @NotNull ExecutionEnvironment env)
    throws ExecutionException {
    final ExecutionResult result = state.execute(executor, this);
    final XDebugSession debugSession = XDebuggerManager.getInstance(project).startSession(env, new XDebugProcessStarter() {
      @NotNull
      @Override
      public XDebugProcess start(@NotNull XDebugSession session) throws ExecutionException {
        //session.setAutoInitBreakpoints(false); // todo[vova]: ?
        final ExecutionResult result = state.execute(executor, GdbRunner.this);
        assert result != null;
        return new GdbDebugProcess(session, (GdbExecutionResult)result);
      }
    });

    
    String sdkHomePath = GoSdkService.getInstance(project).getSdkHomePath(null);
    if (StringUtil.isEmpty(sdkHomePath)) {
      debugSession.stop();
      return null;
    }

    GdbDebugProcess debugProcess = ((GdbDebugProcess)debugSession.getDebugProcess());

    Gdb gdb = debugProcess.getGdb();
    gdb.sendCommand(SET_AUTO_LOAD_SAFE_PATH + sdkHomePath);
    if (SystemInfo.isLinux) gdb.sendCommand(SET_AUTO_LOAD_SAFE_PATH + "/usr/share/go");
    assert result != null;
    gdb.sendCommand("file " + ((GdbExecutionResult)result).getConfiguration().APP_PATH);
    debugSession.initBreakpoints();
    gdb.sendCommand("run > /dev/null"); // todo: collect all output to file and show in the console
    return debugSession.getRunContentDescriptor();
  }
}
