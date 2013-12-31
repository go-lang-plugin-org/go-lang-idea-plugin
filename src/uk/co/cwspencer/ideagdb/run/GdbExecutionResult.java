package uk.co.cwspencer.ideagdb.run;

import com.intellij.execution.DefaultExecutionResult;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.ui.ExecutionConsole;
import org.jetbrains.annotations.NotNull;

public class GdbExecutionResult extends DefaultExecutionResult {
    public GdbRunConfiguration m_configuration;

    public GdbExecutionResult(ExecutionConsole console, @NotNull ProcessHandler processHandler,
                              GdbRunConfiguration configuration) {
        super(console, processHandler);
        m_configuration = configuration;
    }

    public GdbRunConfiguration getConfiguration() {
        return m_configuration;
    }
}
