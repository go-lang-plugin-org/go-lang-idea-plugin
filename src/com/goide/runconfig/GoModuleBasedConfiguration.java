package com.goide.runconfig;

import com.intellij.execution.configurations.RunConfigurationModule;
import com.intellij.openapi.project.Project;

public class GoModuleBasedConfiguration extends RunConfigurationModule {
  public GoModuleBasedConfiguration(Project project) {
    super(project);
  }
}