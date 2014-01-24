package com.goide.debugger.ideagdb.run;

import com.intellij.execution.DefaultExecutionResult;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.ui.ExecutionConsole;
import org.jetbrains.annotations.NotNull;

public class GdbExecutionResult extends DefaultExecutionResult {
  public GdbRunConfiguration myConfiguration;

  public GdbExecutionResult(ExecutionConsole console,
                            @NotNull ProcessHandler processHandler,
                            GdbRunConfiguration configuration) {
    super(console, processHandler);
    myConfiguration = configuration;
  }

  public GdbRunConfiguration getConfiguration() {
    return myConfiguration;
  }
}
