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
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.EmptyRunnable;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.xdebugger.DefaultDebugProcessHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.GoIcons;
import ro.redeul.google.go.config.sdk.GoSdkData;
import ro.redeul.google.go.sdk.GoSdkUtil;

import java.io.File;
import java.util.Map;

public class GdbRunProfileState implements RunProfileState {
    private static final Logger m_log = Logger.getInstance("#uk.co.cwspencer.ideagdb.run.GdbRunProfileState");

    private static final String ID = "Go Console";
    private static final String TITLE = "build";
    private static ConsoleView consoleView;

    private Project project;
    private ExecutionEnvironment m_env;
    private GdbRunConfiguration m_configuration;

    public GdbRunProfileState(Project _project, @NotNull ExecutionEnvironment env, GdbRunConfiguration configuration) {
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

        String goExecName = sdkData.GO_BIN_PATH;

        String projectDir = project.getBasePath();

        if (projectDir == null) {
            throw new CantRunException("Could not retrieve the project directory");
        }

        Map<String,String> sysEnv = GoSdkUtil.getExtendedSysEnv(sdkData, projectDir, m_configuration.envVars);

        if (m_configuration.goVetEnabled) {
            try {
                ToolWindowManager manager = ToolWindowManager.getInstance(project);
                ToolWindow window = manager.getToolWindow(ID);

                if (consoleView == null) {
                    consoleView = TextConsoleBuilderFactory.getInstance().createBuilder(project).getConsole();
                }

                if (window == null) {
                    window = manager.registerToolWindow(ID, false, ToolWindowAnchor.BOTTOM);

                    ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
                    Content content = contentFactory.createContent(consoleView.getComponent(), "", false);
                    window.getContentManager().addContent(content);
                    window.setIcon(GoIcons.GO_ICON_13x13);
                    window.setToHideOnEmptyContent(true);
                    window.setTitle(TITLE);

                }

                window.show(EmptyRunnable.getInstance());

                String[] goEnv = GoSdkUtil.convertEnvMapToArray(sysEnv);

                String command = String.format(
                        "%s vet ./...",
                        goExecName
                );

                Runtime rt = Runtime.getRuntime();
                Process proc = rt.exec(command, goEnv, new File(projectDir));
                OSProcessHandler handler = new OSProcessHandler(proc, null);
                consoleView.attachToProcess(handler);
                consoleView.print(String.format("%s%n", command), ConsoleViewContentType.NORMAL_OUTPUT);
                handler.startNotify();

                if (proc.waitFor() == 0) {
                    VirtualFileManager.getInstance().syncRefresh();

                    consoleView.print(String.format("%nFinished running go vet on project %s%n", projectDir), ConsoleViewContentType.NORMAL_OUTPUT);
                } else {
                    consoleView.print(String.format("%nCouldn't vet project %s%n", projectDir), ConsoleViewContentType.ERROR_OUTPUT);

                    throw new CantRunException(String.format("Error while processing %s vet command.", goExecName));
                }


            } catch (Exception e) {
                e.printStackTrace();
                Messages.showErrorDialog(String.format("Error while processing %s vet command.", goExecName), "Error on Google Go Plugin");

                throw new CantRunException(String.format("Error while processing %s vet command.", goExecName));
            }
        }

        // Build and run
        String execName = m_configuration.goOutputDir.concat("/").concat(project.getName());

        if (GoSdkUtil.isHostOsWindows()) {
            execName = execName.concat(".exe");
        }

        try {
            ToolWindowManager manager = ToolWindowManager.getInstance(project);
            ToolWindow window = manager.getToolWindow(ID);

            if (consoleView == null) {
                consoleView = TextConsoleBuilderFactory.getInstance().createBuilder(project).getConsole();
            }

            if (window == null) {
                window = manager.registerToolWindow(ID, false, ToolWindowAnchor.BOTTOM);

                ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
                Content content = contentFactory.createContent(consoleView.getComponent(), "", false);
                window.getContentManager().addContent(content);
                window.setIcon(GoIcons.GO_ICON_13x13);
                window.setToHideOnEmptyContent(true);
                window.setTitle(TITLE);

            }

            window.show(EmptyRunnable.getInstance());

            String[] goEnv = GoSdkUtil.convertEnvMapToArray(sysEnv);
            String[] command = GoSdkUtil.computeGoBuildCommand(goExecName, m_configuration.builderArguments, execName, m_configuration.scriptName);

            Runtime rt = Runtime.getRuntime();
            Process proc = rt.exec(command, goEnv);
            OSProcessHandler handler = new OSProcessHandler(proc, null);
            consoleView.attachToProcess(handler);
            consoleView.print(String.format("%s%n", StringUtil.join(command, " ")), ConsoleViewContentType.NORMAL_OUTPUT);
            handler.startNotify();

            if (proc.waitFor() == 0) {
                VirtualFileManager.getInstance().syncRefresh();

                consoleView.print(String.format("%nFinished building project %s%n", execName), ConsoleViewContentType.NORMAL_OUTPUT);
            } else {
                consoleView.print(String.format("%nCould't build project %s%n", execName), ConsoleViewContentType.ERROR_OUTPUT);
            }


        } catch (Exception e) {
            e.printStackTrace();
            Messages.showErrorDialog(String.format("Error while processing %s build command.", goExecName), "Error on Google Go Plugin");

            throw new CantRunException(String.format("Error while processing %s build command.", goExecName));
        }


        ProcessHandler processHandler = new DefaultDebugProcessHandler();

        // Create the console
        Project project = m_configuration.getProject();
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
