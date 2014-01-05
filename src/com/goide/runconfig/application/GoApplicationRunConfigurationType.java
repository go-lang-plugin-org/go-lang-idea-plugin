package com.goide.runconfig.application;

import com.goide.GoIcons;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationType;
import com.intellij.execution.configurations.ConfigurationTypeBase;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.openapi.extensions.Extensions;
import com.intellij.openapi.project.Project;

public class GoApplicationRunConfigurationType extends ConfigurationTypeBase {

  public GoApplicationRunConfigurationType() {
    super("GoApplicationRunConfiguration", "Go Application", "Go application run configuration", GoIcons.ICON);
    addFactory(new GoFactory(this));
  }

  public static GoApplicationRunConfigurationType getInstance() {
    return Extensions.findExtension(CONFIGURATION_TYPE_EP, GoApplicationRunConfigurationType.class);
  }

  private static class GoFactory extends ConfigurationFactory {
    public GoFactory(ConfigurationType type) {
      super(type);
    }

    public RunConfiguration createTemplateConfiguration(Project project) {
      return new GoApplicationConfiguration(project, "Go", getInstance());
    }
  }
}
