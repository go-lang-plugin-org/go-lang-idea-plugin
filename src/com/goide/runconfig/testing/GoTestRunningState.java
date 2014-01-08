package com.goide.runconfig.testing;

import com.goide.jps.model.JpsGoSdkType;
import com.goide.runconfig.GoRunningState;
import com.intellij.execution.DefaultExecutionResult;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionResult;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.filters.TextConsoleBuilder;
import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.ProgramRunner;
import com.intellij.execution.testframework.autotest.ToggleAutoTestAction;
import com.intellij.execution.testframework.sm.SMTestRunnerConnectionUtil;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.projectRoots.Sdk;
import org.jetbrains.annotations.NotNull;

public class GoTestRunningState extends GoRunningState {
  private GoTestConfiguration myConfiguration;

  @NotNull
  @Override
  public ExecutionResult execute(@NotNull Executor executor, @NotNull ProgramRunner runner) throws ExecutionException {
    ProcessHandler processHandler = startProcess();
    TextConsoleBuilder consoleBuilder = TextConsoleBuilderFactory.getInstance().createBuilder(myModule.getProject());
    setConsoleBuilder(consoleBuilder);

    GoTestConsoleProperties consoleProperties = new GoTestConsoleProperties(myConfiguration, executor);
    ConsoleView consoleView = SMTestRunnerConnectionUtil.createAndAttachConsole("Go", processHandler, consoleProperties, getEnvironment());

    DefaultExecutionResult executionResult = new DefaultExecutionResult(consoleView, processHandler);
    executionResult.setRestartActions(new ToggleAutoTestAction(getEnvironment()));
    return executionResult;
  }

  public GoTestRunningState(ExecutionEnvironment env, Module module, GoTestConfiguration configuration) {
    super(env, module);
    myConfiguration = configuration;
  }

  @Override
  protected GeneralCommandLine getCommand(@NotNull Sdk sdk) throws ExecutionException {
    String homePath = sdk.getHomePath();
    assert homePath != null;

    String executable = JpsGoSdkType.getGoExecutableFile(sdk.getHomePath()).getAbsolutePath();

    GeneralCommandLine installDependencies = new GeneralCommandLine();
    installDependencies.setExePath(executable);
    installDependencies.addParameter("test");
    installDependencies.addParameter("-i");
    installDependencies.setWorkDirectory(myConfiguration.getWorkingDirectory());
    try {
      installDependencies.createProcess().waitFor();
    }
    catch (InterruptedException ignore) {
    }

    GeneralCommandLine runTests = new GeneralCommandLine();
    runTests.setExePath(executable);
    runTests.addParameter("test");
    runTests.addParameter("-v");
    runTests.addParameter("-run=" + myConfiguration.getTestFilter().trim());
    runTests.getParametersList().addParametersString(myConfiguration.getParams());
    runTests.setWorkDirectory(myConfiguration.getWorkingDirectory());


    return runTests;
  }
}
