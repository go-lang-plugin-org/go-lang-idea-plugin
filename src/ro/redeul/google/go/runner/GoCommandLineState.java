package ro.redeul.google.go.runner;

import com.intellij.execution.*;
import com.intellij.execution.configurations.CommandLineState;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.ProgramRunner;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.EmptyRunnable;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.GoIcons;
import ro.redeul.google.go.config.sdk.GoSdkData;
import ro.redeul.google.go.runner.ui.properties.GoTestConsoleProperties;
import ro.redeul.google.go.sdk.GoSdkUtil;

import java.io.File;
import java.util.Map;

import static com.intellij.execution.testframework.sm.SMTestRunnerConnectionUtil.createAndAttachConsole;

class GoCommandLineState extends CommandLineState {
    private static final String ID = "Go Console";
    private static final String TITLE = "build";
    private static ConsoleView consoleView;

    private final GoTestConsoleProperties consoleProperties;

    public GoCommandLineState(GoTestConsoleProperties consoleProperties, ExecutionEnvironment env) {
        super(env);
        this.consoleProperties = consoleProperties;
    }

    @NotNull
    @Override
    protected OSProcessHandler startProcess() throws ExecutionException {
        GoTestConfiguration testConfiguration = consoleProperties.getConfiguration();
        Project project = testConfiguration.getProject();
        Sdk sdk = GoSdkUtil.getGoogleGoSdkForProject(project);
        if ( sdk == null ) {
            throw new CantRunException("No Go Sdk defined for this project");
        }

        final GoSdkData sdkData = (GoSdkData)sdk.getSdkAdditionalData();
        if ( sdkData == null ) {
            throw new CantRunException("No Go Sdk defined for this project");
        }

        String projectDir = project.getBasePath();

        if (projectDir == null) {
            throw new CantRunException("Could not retrieve the project directory");
        }

        String goExecName = sdkData.GO_BIN_PATH;
        String workingDir = testConfiguration.workingDir;
        Map<String,String> sysEnv = GoSdkUtil.getExtendedSysEnv(sdkData, projectDir, testConfiguration.envVars);

        if (testConfiguration.goVetEnabled) {
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

        // Install dependencies
        GeneralCommandLine testInstallDependencies = new GeneralCommandLine();
        testInstallDependencies.setExePath(goExecName);
        testInstallDependencies.addParameter("test");
        testInstallDependencies.addParameter("-i");

        if (testConfiguration.testArgs != null && testConfiguration.testArgs.trim().length() > 0) {
            testInstallDependencies.getParametersList().addParametersString(testConfiguration.testArgs);
        }

        if (testConfiguration.testTargetType.equals(GoTestConfiguration.TestTargetType.Package)) {
            testInstallDependencies.addParameter(testConfiguration.packageName);
        } else if (testConfiguration.testTargetType.equals(GoTestConfiguration.TestTargetType.File)) {
            testInstallDependencies.addParameter(testConfiguration.testFile);
        } else if (testConfiguration.testTargetType.equals(GoTestConfiguration.TestTargetType.CWD)) {
            testInstallDependencies.addParameter("./...");
        }

        testInstallDependencies.getEnvironment().putAll(sysEnv);
        testInstallDependencies.setWorkDirectory(workingDir);
        try {
            if (testInstallDependencies.createProcess().waitFor() == 0) {
                VirtualFileManager.getInstance().syncRefresh();
            }
        } catch (InterruptedException ignored) {
        }

        // Run the test
        GeneralCommandLine commandLine = new GeneralCommandLine();
        commandLine.setExePath(goExecName);
        commandLine.addParameter("test");
        commandLine.addParameter("-v");
        if (testConfiguration.useShortRun)
            commandLine.addParameter("-short");

        switch (testConfiguration.executeWhat) {
            case Test:
                if (testConfiguration.filter != null && !testConfiguration.filter.isEmpty())
                    commandLine.addParameter("-run=" + testConfiguration.filter.trim());
                break;
            case Benchmark:
                String benchmarkName = ".*";

                if (testConfiguration.filter != null && !testConfiguration.filter.isEmpty())
                    benchmarkName = testConfiguration.filter.trim();
                if (!testConfiguration.testBeforeBenchmark) {
                    commandLine.addParameter("-run=NONE");
                }
                commandLine.addParameter("-bench=" + benchmarkName);
                break;
        }

        if (testConfiguration.testRunnerArgs != null && testConfiguration.testRunnerArgs.trim().length() > 0) {
            commandLine.getParametersList().addParametersString(testConfiguration.testRunnerArgs);
        }

        if (testConfiguration.testTargetType.equals(GoTestConfiguration.TestTargetType.Package)) {
            commandLine.addParameter(testConfiguration.packageName);
        } else if (testConfiguration.testTargetType.equals(GoTestConfiguration.TestTargetType.File)) {
            commandLine.addParameter(testConfiguration.testFile);
        } else if (testConfiguration.testTargetType.equals(GoTestConfiguration.TestTargetType.CWD)) {
            commandLine.addParameter("./...");
        }

        commandLine.getEnvironment().putAll(sysEnv);
        commandLine.setWorkDirectory(workingDir);

        if (testConfiguration.testArgs != null && testConfiguration.testArgs.trim().length() > 0) {
            commandLine.getParametersList().addParametersString(testConfiguration.testArgs);
        }

        return GoApplicationProcessHandler.runCommandLine(commandLine);
    }

    private String getSdkHomePath(GoSdkData sdkData) {
        if (sdkData.GO_GOROOT_PATH.isEmpty()) {
            return new File(sdkData.GO_BIN_PATH).getParentFile().getParent();
        }
        return sdkData.GO_GOROOT_PATH;
    }

    @NotNull
    @Override
    public ExecutionResult execute(@NotNull Executor executor, @NotNull ProgramRunner runner) throws ExecutionException {
        ProcessHandler processHandler = startProcess();
        String packageDir = consoleProperties.getConfiguration().packageDir;
        processHandler.addProcessListener(new GoTestProcessListener(processHandler, packageDir));

        ConsoleView console = createAndAttachConsole("GoTest", processHandler, consoleProperties, getEnvironment());
        Project project = consoleProperties.getProject();
        console.addMessageFilter(new GoConsoleFilter(project, packageDir));
        return new DefaultExecutionResult(console, processHandler, createActions(console, processHandler, executor));
    }
}
