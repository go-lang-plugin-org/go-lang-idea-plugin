package com.goide.debugger.ideagdb.run;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionResult;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.filters.TextConsoleBuilder;
import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.runners.ProgramRunner;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.openapi.project.Project;
import com.intellij.xdebugger.DefaultDebugProcessHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GdbRunProfileState implements RunProfileState {
  private GdbRunConfiguration m_configuration;

  public GdbRunProfileState(GdbRunConfiguration configuration) {
    m_configuration = configuration;
  }

  @Nullable
  @Override
  public ExecutionResult execute(Executor executor, @NotNull ProgramRunner runner)
    throws ExecutionException {
    ProcessHandler processHandler = new DefaultDebugProcessHandler();

    // Create the console
    Project project = m_configuration.getProject();
    final TextConsoleBuilder builder =
      TextConsoleBuilderFactory.getInstance().createBuilder(project);
    ConsoleView m_console = builder.getConsole();

    return new GdbExecutionResult(m_console, processHandler, m_configuration);
  }
}
