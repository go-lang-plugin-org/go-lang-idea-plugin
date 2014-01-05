package com.goide.runconfig.application;

import com.goide.jps.model.JpsGoSdkType;
import com.goide.runconfig.GoRunningState;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.filters.TextConsoleBuilder;
import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.util.io.FileUtil;
import org.jetbrains.annotations.NotNull;

public class GoApplicationRunningState extends GoRunningState {
  private GoApplicationConfiguration myConfiguration;

  public GoApplicationRunningState(ExecutionEnvironment env, Module module, GoApplicationConfiguration configuration) {
    super(env, module);
    myConfiguration = configuration;
  }

  @Override
  protected GeneralCommandLine getCommand(@NotNull Sdk sdk) throws ExecutionException {
    GeneralCommandLine commandLine = new GeneralCommandLine();
    String homePath = sdk.getHomePath();
    assert homePath != null;
    String executable = FileUtil.toSystemDependentName(JpsGoSdkType.getGoExecutableFile(homePath).getAbsolutePath());
    commandLine.setExePath(executable);
    commandLine.addParameter("run");
    commandLine.addParameter(myConfiguration.getFilePath());
    commandLine.addParameter(myConfiguration.getParams());
    commandLine.setWorkDirectory(myModule.getProject().getBasePath());
    TextConsoleBuilder consoleBuilder = TextConsoleBuilderFactory.getInstance().createBuilder(myModule.getProject());
    setConsoleBuilder(consoleBuilder);
    return commandLine;
  }
}
