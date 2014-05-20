package ro.redeul.google.go.runner;

import com.intellij.execution.CantRunException;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.CommandLineState;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFileManager;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.config.sdk.GoSdkData;
import ro.redeul.google.go.ide.ui.GoToolWindow;
import ro.redeul.google.go.sdk.GoSdkUtil;

import java.io.File;
import java.util.Map;

/**
 * Created by d3xter on 28.02.14.
 */
public class GoRunProfileState extends CommandLineState {
    public GoApplicationConfiguration m_configuration;
    public Project m_project;

    private static final String TITLE = "Build";

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

        String goExecName = sdkData.GO_EXEC;

        String projectDir = m_project.getBasePath();

        if (projectDir == null) {
            throw new CantRunException("Could not retrieve the project directory");
        }

        Map<String,String> sysEnv = GoSdkUtil.getExtendedSysEnv(sdkData, projectDir, m_configuration.envVars);

        GoToolWindow toolWindow = GoToolWindow.getInstance(m_project);
        toolWindow.setTitle(TITLE);

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

        if (execName.endsWith(".go")) {
            execName = execName.substring(0, execName.length() - 3);
        }

        if (GoSdkUtil.isHostOsWindows()) {
            execName = execName.concat(".exe");
        }

        try {
            String[] goEnv = GoSdkUtil.convertEnvMapToArray(sysEnv);

            String[] command = GoSdkUtil.computeGoBuildCommand(goExecName, m_configuration.runBuilderArguments, execName, m_configuration.scriptName);

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
