package com.goide.runconfig.application;

import com.goide.GoIcons;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationTypeBase;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.openapi.extensions.Extensions;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public class GoApplicationRunConfigurationType extends ConfigurationTypeBase {
  public GoApplicationRunConfigurationType() {
    super("GoApplicationRunConfiguration", "Go Application", "Go application run configuration", GoIcons.APPLICATION_RUN);
    addFactory(new ConfigurationFactory(this) {
      @NotNull
      public RunConfiguration createTemplateConfiguration(Project project) {
        return new GoApplicationConfiguration(project, "Go", getInstance());
      }
    });
  }

  @NotNull
  public static GoApplicationRunConfigurationType getInstance() {
    return Extensions.findExtension(CONFIGURATION_TYPE_EP, GoApplicationRunConfigurationType.class);
  }
}
