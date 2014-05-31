package com.goide.runconfig.file;

import com.goide.jps.model.JpsGoSdkType;
import com.goide.runconfig.GoRunningState;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.configurations.ParametersList;
import com.intellij.execution.filters.TextConsoleBuilder;
import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.util.PathUtil;
import org.jetbrains.annotations.NotNull;

public class GoRunFileRunningState extends GoRunningState {
  private GoRunFileConfiguration myConfiguration;

  public GoRunFileRunningState(@NotNull ExecutionEnvironment env, @NotNull Module module, GoRunFileConfiguration configuration) {
    super(env, module);
    myConfiguration = configuration;
  }

  @NotNull
  @Override
  protected GeneralCommandLine getCommand(@NotNull Sdk sdk) throws ExecutionException {
    GeneralCommandLine commandLine = new GeneralCommandLine();
    String homePath = sdk.getHomePath();
    assert homePath != null;
    String executable = JpsGoSdkType.getGoExecutableFile(homePath).getAbsolutePath();
    commandLine.setExePath(executable);
    ParametersList list = commandLine.getParametersList();
    list.add("run");
    String filePath = myConfiguration.getFilePath();
    list.addParametersString(filePath);
    commandLine.setWorkDirectory(PathUtil.getParentPath(filePath));
    TextConsoleBuilder consoleBuilder = TextConsoleBuilderFactory.getInstance().createBuilder(myModule.getProject());
    setConsoleBuilder(consoleBuilder);
    return commandLine;
  }
}
