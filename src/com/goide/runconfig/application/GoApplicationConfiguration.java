package com.goide.runconfig.application;

import com.goide.runconfig.GoModuleBasedConfiguration;
import com.goide.runconfig.GoRunConfigurationWithMain;
import com.goide.runconfig.GoRunner;
import com.goide.runconfig.ui.GoRunConfigurationEditorForm;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.ConfigurationType;
import com.intellij.execution.configurations.ModuleBasedConfiguration;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public class GoApplicationConfiguration extends GoRunConfigurationWithMain<GoApplicationRunningState> {
  public GoApplicationConfiguration(Project project, String name, ConfigurationType configurationType) {
    super(name, new GoModuleBasedConfiguration(project), configurationType.getConfigurationFactories()[0]);
  }

  @Override
  protected ModuleBasedConfiguration createInstance() {
    return new GoApplicationConfiguration(getProject(), getName(), GoApplicationRunConfigurationType.getInstance());
  }

  @NotNull
  @Override
  public SettingsEditor<? extends RunConfiguration> getConfigurationEditor() {
    return new GoRunConfigurationEditorForm(getProject(), true);
  }

  @Override
  public RunProfileState getState(@NotNull Executor executor, @NotNull ExecutionEnvironment env) throws ExecutionException {
    return GoRunner.EMPTY_RUN_STATE;
  }

  @Override
  protected GoApplicationRunningState newRunningState(ExecutionEnvironment env, Module module) {
    return new GoApplicationRunningState(env, module, this);
  }
}
