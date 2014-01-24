package com.goide.debugger.ideagdb.run;

import com.intellij.execution.configurations.RunConfigurationModule;
import com.intellij.openapi.project.Project;

public class GdbRunConfigurationModule extends RunConfigurationModule {
  public GdbRunConfigurationModule(Project project) {
    super(project);
  }
}
