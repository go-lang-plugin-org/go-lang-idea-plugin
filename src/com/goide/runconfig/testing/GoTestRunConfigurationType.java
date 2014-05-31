package com.goide.runconfig.testing;

import com.goide.GoIcons;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationType;
import com.intellij.execution.configurations.ConfigurationTypeBase;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.openapi.extensions.Extensions;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public class GoTestRunConfigurationType extends ConfigurationTypeBase {

  public GoTestRunConfigurationType() {
    super("GoTestRunConfiguration", "Go Test", "Go test run configuration", GoIcons.TEST_RUN);
    addFactory(new GoFactory(this));
  }

  @NotNull
  public static GoTestRunConfigurationType getInstance() {
    return Extensions.findExtension(CONFIGURATION_TYPE_EP, GoTestRunConfigurationType.class);
  }

  private static class GoFactory extends ConfigurationFactory {
    public GoFactory(@NotNull ConfigurationType type) {
      super(type);
    }

    @NotNull
    public RunConfiguration createTemplateConfiguration(@NotNull Project project) {
      return new GoTestRunConfiguration(project, "Go Test", getInstance());
    }
  }
}
