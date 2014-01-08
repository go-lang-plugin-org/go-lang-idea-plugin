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

public class GoTestConfiguration extends GoRunConfigurationBase<GoTestRunningState> {
  private String myParams = "";
  private String myTestFilter = "";
  private String myWorkingDirectory;

  public GoTestConfiguration(Project project, String name, ConfigurationType configurationType) {
    super(name, new GoModuleBasedConfiguration(project), configurationType.getConfigurationFactories()[0]);
    Module module = getConfigurationModule().getModule();
    myWorkingDirectory = module != null ? PathUtil.getParentPath(module.getModuleFilePath()) : project.getBasePath();
  }

  @Override
  protected ModuleBasedConfiguration createInstance() {
    return new GoTestConfiguration(getProject(), getName(), GoTestRunConfigurationType.getInstance());
  }

  @NotNull
  @Override
  public SettingsEditor<? extends RunConfiguration> getConfigurationEditor() {
    return new GoTestRunConfigurationEditorForm();
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
  public String getTestFilter() {
    return myTestFilter;
  }

  public void setTestFilter(@NotNull String testFilter) {
    myTestFilter = testFilter;
  }

  @NotNull
  public String getWorkingDirectory() {
    return myWorkingDirectory;
  }

  public void setWorkingDirectory(@NotNull String workingDirectory) {
    myWorkingDirectory = workingDirectory;
  }
}
