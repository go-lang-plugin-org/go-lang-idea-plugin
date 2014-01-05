package com.goide.runconfig.application;

import com.goide.jps.model.JpsGoSdkType;
import com.goide.runconfig.GoRunningState;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.filters.TextConsoleBuilder;
import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.compiler.CompilerPaths;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.util.PathUtil;
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
    String outputDirectory = CompilerPaths.getModuleOutputPath(myModule, false);
    String modulePath = PathUtil.getParentPath(myModule.getModuleFilePath());
    String executable = FileUtil.toSystemDependentName(JpsGoSdkType.getBinaryPathByModulePath(modulePath, outputDirectory));
    commandLine.setExePath(executable);
    commandLine.addParameter(myConfiguration.getParams());
    commandLine.setWorkDirectory(PathUtil.getParentPath(executable));
    TextConsoleBuilder consoleBuilder = TextConsoleBuilderFactory.getInstance().createBuilder(myModule.getProject());
    setConsoleBuilder(consoleBuilder);
    return commandLine;
  }
}
