package ro.redeul.google.go.runner;

import java.io.File;
import java.util.HashMap;

import com.intellij.execution.CantRunException;
import com.intellij.execution.DefaultExecutionResult;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionResult;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.CommandLineState;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.ProgramRunner;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.config.sdk.GoSdkData;
import ro.redeul.google.go.runner.ui.properties.GoTestConsoleProperties;
import ro.redeul.google.go.sdk.GoSdkUtil;
import static com.intellij.execution.testframework.sm.SMTestRunnerConnectionUtil.createAndAttachConsole;
import static ro.redeul.google.go.sdk.GoSdkUtil.prependToGoPath;

class GoCommandLineState extends CommandLineState {
    private GoTestConsoleProperties consoleProperties;

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

        if ( cfg.getModule() == null || cfg.getModule().getModuleFile() == null ) {
            throw new CantRunException("No module selected for this test configuration");
        }

        final VirtualFile moduleFile = cfg.getModule().getModuleFile();
        if ( moduleFile == null || moduleFile.getParent() == null) {
            throw new CantRunException("The module does not have a valid parent folder");
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
        commandLine.setEnvParams(new HashMap<String, String>() {{
            put("GOPATH", prependToGoPath(moduleFile.getParent().getCanonicalPath()));
            put("GOROOT", getSdkHomePath(sdkData));
        }});

        return GoApplicationProcessHandler.runCommandLine(commandLine);
    }

    private String getSdkHomePath(GoSdkData sdkData) {
        if (sdkData.GO_HOME_PATH.isEmpty()) {
            return new File(sdkData.GO_BIN_PATH).getParent();
        }
        return sdkData.GO_HOME_PATH;
    }

    @Override
    public ExecutionResult execute(@NotNull Executor executor, @NotNull ProgramRunner runner) throws ExecutionException {
        ProcessHandler processHandler = startProcess();
        String packageDir = consoleProperties.getConfiguration().packageDir;
        processHandler.addProcessListener(new GoTestProcessListener(processHandler, packageDir));

        ConsoleView console = createAndAttachConsole("GoTest", processHandler, consoleProperties,
                getRunnerSettings(), getConfigurationSettings());
        Project project = consoleProperties.getProject();
        console.addMessageFilter(new GoTestConsoleFilter(project, packageDir));
        return new DefaultExecutionResult(console, processHandler, createActions(console, processHandler, executor));
    }
}
