package com.goide.runconfig;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionResult;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.RunProfile;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.executors.DefaultRunExecutor;
import com.intellij.execution.runners.DefaultProgramRunner;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.ProgramRunner;
import com.intellij.execution.runners.RunContentBuilder;
import com.intellij.execution.ui.RunContentDescriptor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GoRunner extends DefaultProgramRunner {
  public static final String GO_RUNNER_ID = "GoRunner";

  public static final RunProfileState EMPTY_RUN_STATE = new RunProfileState() {
    @Override
    public ExecutionResult execute(final Executor executor, @NotNull final ProgramRunner runner) throws ExecutionException {
      return null;
    }
  };

  @NotNull
  @Override
  public String getRunnerId() {
    return GO_RUNNER_ID;
  }

  @Override
  public boolean canRun(@NotNull String executorId, @NotNull RunProfile profile) {
    return DefaultRunExecutor.EXECUTOR_ID.equals(executorId) && profile instanceof GoRunConfigurationBase;
  }

  @Override
  @NotNull
  protected RunContentDescriptor doExecute(@NotNull Project project,
                                           @NotNull RunProfileState state,
                                           @Nullable RunContentDescriptor contentToReuse,
                                           @NotNull ExecutionEnvironment env) throws ExecutionException {
    GoRunConfigurationBase configuration = (GoRunConfigurationBase)env.getRunProfile();
    GoRunningState runningState = configuration.createRunningState(env);
    FileDocumentManager.getInstance().saveAllDocuments();
    ExecutionResult executionResult = runningState.execute(env.getExecutor(), this);
    return new RunContentBuilder(this, executionResult, env).showRunContent(contentToReuse);
  }
}