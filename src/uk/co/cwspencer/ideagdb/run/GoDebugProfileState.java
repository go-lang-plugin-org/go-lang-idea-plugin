package uk.co.cwspencer.ideagdb.run;

import com.intellij.execution.CantRunException;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionResult;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.ConfigurationPerRunnerSettings;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.configurations.RunnerSettings;
import com.intellij.execution.filters.TextConsoleBuilder;
import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.ProgramRunner;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.xdebugger.DefaultDebugProcessHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.config.sdk.GoSdkData;
import ro.redeul.google.go.ide.ui.GoToolWindow;
import ro.redeul.google.go.runner.GoApplicationConfiguration;
import ro.redeul.google.go.sdk.GoSdkUtil;

import java.io.File;
import java.util.Map;

public class GoDebugProfileState implements RunProfileState {
    private static final Logger m_log = Logger.getInstance("#uk.co.cwspencer.ideagdb.run.GoDebugProfileState");

    private static final String ID = "Go Console";
    private static final String TITLE = "Debug";

    private Project project;
    private ExecutionEnvironment m_env;
    private GoApplicationConfiguration m_configuration;

    public GoDebugProfileState(Project _project, @NotNull ExecutionEnvironment env, GoApplicationConfiguration configuration) {
        project = _project;
        m_env = env;
        m_configuration = configuration;
    }

    @Nullable
    @Override
    public ExecutionResult execute(Executor executor, @NotNull ProgramRunner runner)
            throws ExecutionException {

        Sdk sdk = GoSdkUtil.getGoogleGoSdkForProject(project);
        if ( sdk == null ) {
            throw new CantRunException("No Go Sdk defined for this project");
        }

        final GoSdkData sdkData = (GoSdkData)sdk.getSdkAdditionalData();
        if ( sdkData == null ) {
            throw new CantRunException("No Go Sdk defined for this project");
        }

        String goExecName = sdkData.GO_EXEC;

        String projectDir = project.getBasePath();

        if (projectDir == null) {
            throw new CantRunException("Could not retrieve the project directory");
        }

        Map<String,String> sysEnv = GoSdkUtil.getExtendedSysEnv(sdkData, projectDir, m_configuration.envVars);

        GoToolWindow toolWindow = GoToolWindow.getInstance(project);
        toolWindow.setTitle(TITLE);

        // Build and run
        String execName = m_configuration.goOutputDir.concat("/").concat(project.getName());

        if (execName.endsWith(".go")) {
            execName = execName.substring(0, execName.length() - 3);
        }

        if (GoSdkUtil.isHostOsWindows()) {
            execName = execName.concat(".exe");
        }

        try {
            String[] goEnv = GoSdkUtil.convertEnvMapToArray(sysEnv);

            String[] command = GoSdkUtil.computeGoBuildCommand(goExecName, m_configuration.debugBuilderArguments, execName, m_configuration.scriptName);

            Runtime rt = Runtime.getRuntime();
            Process proc = rt.exec(command, goEnv, new File(projectDir));
            OSProcessHandler handler = new OSProcessHandler(proc, null);
            toolWindow.attachConsoleViewToProcess(handler);
            toolWindow.printNormalMessage(String.format("%s%n", StringUtil.join(command, " ")));
            handler.startNotify();

            if (proc.waitFor() == 0) {
                VirtualFileManager.getInstance().syncRefresh();

                toolWindow.printNormalMessage(String.format("%nFinished building project %s%n", execName));
            } else {
                toolWindow.printErrorMessage(String.format("%nCould't build project %s%n", execName));
            }


        } catch (Exception e) {
            e.printStackTrace();
            Messages.showErrorDialog(String.format("Error while processing %s build command.", goExecName), "Error on Google Go Plugin");

            throw new CantRunException(String.format("Error while processing %s build command.", goExecName));
        }


        ProcessHandler processHandler = new DefaultDebugProcessHandler();

        // Create the console
        final TextConsoleBuilder builder = TextConsoleBuilderFactory.getInstance().createBuilder(project);
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
