package com.goide.runconfig.testing;

import com.goide.runconfig.GoModuleBasedConfiguration;
import com.goide.runconfig.GoRunConfigurationBase;
import com.goide.runconfig.GoRunner;
import com.goide.runconfig.testing.ui.GoTestRunConfigurationEditorForm;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.*;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.util.PathUtil;
import org.jetbrains.annotations.NotNull;

public class GoTestRunConfiguration extends GoRunConfigurationBase<GoTestRunningState> {
  private String myPackage = "";
  private String myFilePath = "";
  private String myDirectoryPath = "";

  private String myParams = "";
  private String myPattern = "";
  private String myWorkingDirectory;
  private Kind myKind = Kind.DIRECTORY;

  public GoTestRunConfiguration(Project project, String name, ConfigurationType configurationType) {
    super(name, new GoModuleBasedConfiguration(project), configurationType.getConfigurationFactories()[0]);
    Module module = getConfigurationModule().getModule();
    myWorkingDirectory = module != null ? PathUtil.getParentPath(module.getModuleFilePath()) : project.getBasePath();
  }

  @Override
  protected ModuleBasedConfiguration createInstance() {
    return new GoTestRunConfiguration(getProject(), getName(), GoTestRunConfigurationType.getInstance());
  }

  @NotNull
  @Override
  public SettingsEditor<? extends RunConfiguration> getConfigurationEditor() {
    return new GoTestRunConfigurationEditorForm(getProject());
  }

  @Override
  public RunProfileState getState(@NotNull Executor executor, @NotNull ExecutionEnvironment env) throws ExecutionException {
    return GoRunner.EMPTY_RUN_STATE;
  }

  @Override
  protected GoTestRunningState newRunningState(ExecutionEnvironment env, Module module) {
    return new GoTestRunningState(env, module, this);
  }

  @Override
  public void checkConfiguration() throws RuntimeConfigurationException {
    GoModuleBasedConfiguration configurationModule = getConfigurationModule();
    configurationModule.checkForWarning();
    // todo
  }

  @NotNull
  public String getParams() {
    return myParams;
  }

  public void setParams(@NotNull String params) {
    this.myParams = params;
  }

  @NotNull
  public String getPattern() {
    return myPattern;
  }

  public void setPattern(@NotNull String pattern) {
    myPattern = pattern;
  }

  @NotNull
  public String getWorkingDirectory() {
    return myWorkingDirectory;
  }

  public void setWorkingDirectory(@NotNull String workingDirectory) {
    myWorkingDirectory = workingDirectory;
  }

  @NotNull
  public Kind getKind() {
    return myKind;
  }

  public void setKind(@NotNull Kind kind) {
    myKind = kind;
  }

  @NotNull
  public String getPackage() {
    return myPackage;
  }

  public void setPackage(@NotNull String aPackage) {
    myPackage = aPackage;
  }

  @NotNull
  public String getFilePath() {
    return myFilePath;
  }

  public void setFilePath(@NotNull String filePath) {
    myFilePath = filePath;
  }

  @NotNull
  public String getDirectoryPath() {
    return myDirectoryPath;
  }

  public void setDirectoryPath(@NotNull String directoryPath) {
    myDirectoryPath = directoryPath;
  }

  public enum Kind {
    DIRECTORY, PACKAGE, FILE
  }
}
