package uk.co.cwspencer.ideagdb.run;

import com.intellij.execution.DefaultExecutionResult;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.ui.ExecutionConsole;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.runner.GoApplicationConfiguration;

public class GdbExecutionResult extends DefaultExecutionResult {
    public GoApplicationConfiguration m_configuration;

    public GdbExecutionResult(ExecutionConsole console, @NotNull ProcessHandler processHandler,
                              GoApplicationConfiguration configuration) {
        super(console, processHandler);
        m_configuration = configuration;
    }

    public GoApplicationConfiguration getConfiguration() {
        return m_configuration;
    }
}
