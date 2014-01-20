package com.goide.runconfig.testing;

import com.goide.GoIcons;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationType;
import com.intellij.execution.configurations.ConfigurationTypeBase;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.openapi.extensions.Extensions;
import com.intellij.openapi.project.Project;

public class GoTestRunConfigurationType extends ConfigurationTypeBase {

  public GoTestRunConfigurationType() {
    super("GoTestRunConfiguration", "Go Test", "Go test run configuration", GoIcons.TEST_RUN);
    addFactory(new GoFactory(this));
  }

  public static GoTestRunConfigurationType getInstance() {
    return Extensions.findExtension(CONFIGURATION_TYPE_EP, GoTestRunConfigurationType.class);
  }

  private static class GoFactory extends ConfigurationFactory {
    public GoFactory(ConfigurationType type) {
      super(type);
    }

    public RunConfiguration createTemplateConfiguration(Project project) {
      return new GoTestRunConfiguration(project, "Go Test", getInstance());
    }
  }
}
