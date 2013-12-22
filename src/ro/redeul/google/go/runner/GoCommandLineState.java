package ro.redeul.google.go.runner;

import com.intellij.execution.*;
import com.intellij.execution.configurations.CommandLineState;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.ProgramRunner;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.config.sdk.GoSdkData;
import ro.redeul.google.go.runner.ui.properties.GoTestConsoleProperties;
import ro.redeul.google.go.sdk.GoSdkUtil;

import java.io.File;

import static com.intellij.execution.testframework.sm.SMTestRunnerConnectionUtil.createAndAttachConsole;
import static ro.redeul.google.go.sdk.GoSdkUtil.prependToGoPath;

class GoCommandLineState extends CommandLineState {
    private final GoTestConsoleProperties consoleProperties;

    public GoCommandLineState(GoTestConsoleProperties consoleProperties, ExecutionEnvironment env) {
        super(env);
        this.consoleProperties = consoleProperties;
    }

    @NotNull
    @Override
    protected OSProcessHandler startProcess() throws ExecutionException {
        GeneralCommandLine commandLine = new GeneralCommandLine();

        GoTestConfiguration cfg = consoleProperties.getConfiguration();
        Sdk sdk = GoSdkUtil.getGoogleGoSdkForProject(cfg.getProject());
        if ( sdk == null ) {
            throw new CantRunException("No Go Sdk defined for this project");
        }

        final GoSdkData sdkData = (GoSdkData)sdk.getSdkAdditionalData();
        if ( sdkData == null ) {
            throw new CantRunException("No Go Sdk defined for this project");
        }


        String workingDir = consoleProperties.getProject().getBaseDir().getCanonicalPath();

        GeneralCommandLine testi = new GeneralCommandLine();
        testi.setExePath(sdkData.GO_BIN_PATH);
        testi.addParameter("test");
        testi.addParameter("-i");
        testi.addParameter(cfg.packageName);
        testi.getEnvironment().put("GOPATH", prependToGoPath(workingDir));
        testi.getEnvironment().put("GOROOT", getSdkHomePath(sdkData));
        try {
            testi.createProcess().waitFor();
        } catch (InterruptedException ignored) {
        }

        commandLine.setExePath(sdkData.GO_BIN_PATH);
        commandLine.addParameter("test");
        commandLine.addParameter("-v");
        if (cfg.useShortRun)
            commandLine.addParameter("-short");

        switch (cfg.executeWhat) {
            case Test:
                if (cfg.filter != null && !cfg.filter.isEmpty())
                    commandLine.addParameter("-run=" + cfg.filter.trim());
                break;
            case Benchmark:
                String benchmarkName = ".*";

                if (cfg.filter != null && !cfg.filter.isEmpty())
                    benchmarkName = cfg.filter.trim();
                if (!cfg.testBeforeBenchmark) {
                    commandLine.addParameter("-run=NONE");
                }
                commandLine.addParameter("-bench=" + benchmarkName);
                break;
        }

        commandLine.addParameter(cfg.packageName);
        commandLine.getEnvironment().put("GOPATH", prependToGoPath(workingDir));
        commandLine.getEnvironment().put("GOROOT", getSdkHomePath(sdkData));

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
        console.addMessageFilter(new GoTestConsoleFilter(project, packageDir));
        return new DefaultExecutionResult(console, processHandler, createActions(console, processHandler, executor));
    }
}
