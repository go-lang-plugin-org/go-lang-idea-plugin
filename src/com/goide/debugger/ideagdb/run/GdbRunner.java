package com.goide.debugger.ideagdb.run;

import com.goide.debugger.gdb.Gdb;
import com.goide.debugger.ideagdb.debug.GdbDebugProcess;
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
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.util.SystemInfo;
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
    return createContentDescriptor(env.getProject(), env.getExecutor(), state, env.getContentToReuse(), env);
  }

  @Nullable
  protected RunContentDescriptor createContentDescriptor(@NotNull Project project,
                                                         final Executor executor,
                                                         @NotNull final RunProfileState state,
                                                         RunContentDescriptor contentToReuse,
                                                         @NotNull ExecutionEnvironment env)
    throws ExecutionException {
    final ExecutionResult result = state.execute(executor, this);
    final XDebugSession debugSession = XDebuggerManager.getInstance(project).startSession(this,
                                                                                          env, contentToReuse, new XDebugProcessStarter() {
      @NotNull
      @Override
      public XDebugProcess start(@NotNull XDebugSession session) throws ExecutionException {
        session.setAutoInitBreakpoints(false);
        final ExecutionResult result = state.execute(executor, GdbRunner.this);
        return new GdbDebugProcess(session, (GdbExecutionResult)result);
      }
    });

    Sdk sdk = ProjectRootManager.getInstance(project).getProjectSdk();
    if (sdk == null) {
      debugSession.stop();
      return null;
    }

    GdbDebugProcess debugProcess = ((GdbDebugProcess)debugSession.getDebugProcess());

    Gdb gdb = debugProcess.getGdb();
    String sdkHomePath = sdk.getHomePath();
    if (sdkHomePath != null) gdb.sendCommand(SET_AUTO_LOAD_SAFE_PATH + sdkHomePath);
    if (SystemInfo.isLinux) gdb.sendCommand(SET_AUTO_LOAD_SAFE_PATH + "/usr/share/go");
    gdb.sendCommand("file " + ((GdbExecutionResult)result).getConfiguration().APP_PATH);
    debugSession.initBreakpoints();
    gdb.sendCommand("run > /dev/null"); // todo: collect all output to file and show in the console
    return debugSession.getRunContentDescriptor();
  }
}
