package com.goide.runconfig.file;

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

public class GoRunFileConfiguration extends GoRunConfigurationWithMain<GoRunFileRunningState> {
  public GoRunFileConfiguration(Project project, String name, ConfigurationType configurationType) {
    super(name, new GoModuleBasedConfiguration(project), configurationType.getConfigurationFactories()[0]);
  }

  @Override
  protected ModuleBasedConfiguration createInstance() {
    return new GoRunFileConfiguration(getProject(), getName(), GoRunFileConfigurationType.getInstance());
  }

  @NotNull
  @Override
  public SettingsEditor<? extends RunConfiguration> getConfigurationEditor() {
    return new GoRunConfigurationEditorForm(getProject(), false);
  }

  @Override
  public RunProfileState getState(@NotNull Executor executor, @NotNull ExecutionEnvironment env) throws ExecutionException {
    return GoRunner.EMPTY_RUN_STATE;
  }

  @Override
  protected GoRunFileRunningState newRunningState(ExecutionEnvironment env, Module module) {
    return new GoRunFileRunningState(env, module, this);
  }
}
