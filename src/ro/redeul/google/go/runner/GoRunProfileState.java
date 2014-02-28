package ro.redeul.google.go.runner;

import com.intellij.execution.CantRunException;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.CommandLineState;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.util.EmptyRunnable;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.config.sdk.GoSdkData;
import ro.redeul.google.go.sdk.GoSdkUtil;

import java.io.File;
import java.util.Map;

/**
 * Created by d3xter on 28.02.14.
 */
public class GoRunProfileState extends CommandLineState {
    public GoApplicationConfiguration m_configuration;
    public Project m_project;

    private static final String TITLE = "go build";

    public GoRunProfileState(Project project, @NotNull ExecutionEnvironment env, GoApplicationConfiguration configuration) {
        super(env);

        this.m_project = project;
        this.m_configuration = configuration;
    }

    @NotNull
    @Override
    protected ProcessHandler startProcess() throws ExecutionException {
        Sdk sdk = GoSdkUtil.getGoogleGoSdkForProject(m_project);
        if ( sdk == null ) {
            throw new CantRunException("No Go Sdk defined for this project");
        }

        final GoSdkData sdkData = (GoSdkData)sdk.getSdkAdditionalData();
        if ( sdkData == null ) {
            throw new CantRunException("No Go Sdk defined for this project");
        }

        String goExecName = sdkData.GO_BIN_PATH;

        String projectDir = m_project.getBasePath();

        if (projectDir == null) {
            throw new CantRunException("Could not retrieve the project directory");
        }

        Map<String,String> sysEnv = GoSdkUtil.getExtendedSysEnv(sdkData, projectDir, m_configuration.envVars);

        if (m_configuration.goVetEnabled) {
            try {
                ToolWindowManager manager = ToolWindowManager.getInstance(m_project);
                ToolWindow window = manager.getToolWindow(GoCommonConsoleView.ID);

                if (GoCommonConsoleView.consoleView == null) {
                    GoCommonConsoleView.consoleView = TextConsoleBuilderFactory.getInstance().createBuilder(m_project).getConsole();
                }
                ConsoleView consoleView = GoCommonConsoleView.consoleView;

                if (window == null) {
                    window = manager.registerToolWindow(GoCommonConsoleView.ID, false, ToolWindowAnchor.BOTTOM);

                    ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
                    Content content = contentFactory.createContent(GoCommonConsoleView.consoleView.getComponent(), "", false);
                    window.getContentManager().addContent(content);
                    window.setIcon(GoSdkUtil.getProjectIcon(sdk));
                    window.setToHideOnEmptyContent(true);
                }
                window.setTitle(TITLE);

                window.show(EmptyRunnable.getInstance());

                String[] goEnv = GoSdkUtil.convertEnvMapToArray(sysEnv);

                String command = String.format(
                        "%s vet ./...",
                        goExecName
                );

                Runtime rt = Runtime.getRuntime();
                Process proc = rt.exec(command, goEnv, new File(projectDir));
                OSProcessHandler handler = new OSProcessHandler(proc, null);
                GoCommonConsoleView.consoleView.attachToProcess(handler);
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
                throw new CantRunException(String.format("Error while processing %s vet command.", goExecName));
            }
        }

        if (!m_configuration.goBuildBeforeRun) {
            // Just run
            GeneralCommandLine commandLine = new GeneralCommandLine();

            commandLine.setExePath(goExecName);
            commandLine.addParameter("run");
            if (m_configuration.runBuilderArguments != null && m_configuration.runBuilderArguments.trim().length() > 0) {
                commandLine.getParametersList().addParametersString(m_configuration.runBuilderArguments);
            }

            commandLine.addParameter(m_configuration.scriptName);
            if (m_configuration.scriptArguments != null && m_configuration.scriptArguments.trim().length() > 0) {
                commandLine.getParametersList().addParametersString(m_configuration.scriptArguments);
            }

            commandLine.getEnvironment().putAll(sysEnv);
            commandLine.setWorkDirectory(m_configuration.workingDir);

            return GoApplicationProcessHandler.runCommandLine(commandLine);
        }


        // Build and run
        String execName = m_configuration.goOutputDir.concat("/").concat(m_configuration.getName());

        if (GoSdkUtil.isHostOsWindows()) {
            execName = execName.concat(".exe");
        }

        try {
            ToolWindowManager manager = ToolWindowManager.getInstance(m_project);
            ToolWindow window = manager.getToolWindow(GoCommonConsoleView.ID);

            if (GoCommonConsoleView.consoleView == null) {
                GoCommonConsoleView.consoleView = TextConsoleBuilderFactory.getInstance().createBuilder(m_project).getConsole();
            }
            ConsoleView consoleView = GoCommonConsoleView.consoleView;

            if (window == null) {
                window = manager.registerToolWindow(GoCommonConsoleView.ID, false, ToolWindowAnchor.BOTTOM);

                ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
                Content content = contentFactory.createContent(consoleView.getComponent(), "", false);
                window.getContentManager().addContent(content);
                window.setIcon(GoSdkUtil.getProjectIcon(sdk));
                window.setToHideOnEmptyContent(true);
            }
            window.setTitle(TITLE);

            window.show(EmptyRunnable.getInstance());

            String[] goEnv = GoSdkUtil.convertEnvMapToArray(sysEnv);
            String[] command = GoSdkUtil.computeGoBuildCommand(goExecName, m_configuration.runBuilderArguments, execName, m_configuration.scriptName);

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
                throw new CantRunException(String.format("Error while processing %s build command.", goExecName));
            }
        } catch (Exception e) {
            throw new CantRunException(String.format("Error while processing %s build command.", goExecName));
        }

        // Now run the build
        GeneralCommandLine commandLine = new GeneralCommandLine();

        commandLine.setExePath(execName);
        commandLine.setWorkDirectory(m_configuration.workingDir);
        if (m_configuration.scriptArguments != null && m_configuration.scriptArguments.trim().length() > 0) {
            commandLine.getParametersList().addParametersString(m_configuration.scriptArguments);
        }

        return GoApplicationProcessHandler.runCommandLine(commandLine);
    }
}
