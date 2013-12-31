package uk.co.cwspencer.ideagdb.run;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionResult;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.ConfigurationPerRunnerSettings;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.configurations.RunnerSettings;
import com.intellij.execution.filters.TextConsoleBuilder;
import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.ProgramRunner;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.xdebugger.DefaultDebugProcessHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GdbRunProfileState implements RunProfileState {
    private static final Logger m_log =
            Logger.getInstance("#uk.co.cwspencer.ideagdb.run.GdbRunProfileState");

    private ExecutionEnvironment m_env;
    private GdbRunConfiguration m_configuration;

    public GdbRunProfileState(@NotNull ExecutionEnvironment env, GdbRunConfiguration configuration) {
        m_env = env;
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

    public RunnerSettings getRunnerSettings() {
        m_log.warn("getRunnerSettings: stub");
        return null;
    }

    public ConfigurationPerRunnerSettings getConfigurationSettings() {
        return m_env.getConfigurationSettings();
    }
}
