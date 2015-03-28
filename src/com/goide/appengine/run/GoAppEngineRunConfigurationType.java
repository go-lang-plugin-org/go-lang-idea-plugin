package com.goide.appengine.run;

import com.goide.appengine.GoAppEngineIcons;
import com.goide.runconfig.GoConfigurationFactoryBase;
import com.intellij.execution.configurations.ConfigurationTypeBase;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.openapi.extensions.Extensions;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public class GoAppEngineRunConfigurationType extends ConfigurationTypeBase {

  public GoAppEngineRunConfigurationType() {
    super("GoAppEngineRunConfiguration", "Go App Engine", "Go app engine web server runner", GoAppEngineIcons.ICON);
    addFactory(new GoConfigurationFactoryBase(this) {
      @NotNull
      public RunConfiguration createTemplateConfiguration(@NotNull Project project) {
        return new GoAppEngineRunConfiguration(project, "Go App Engine", getInstance());
      }
    });
  }

  @NotNull
  public static GoAppEngineRunConfigurationType getInstance() {
    return Extensions.findExtension(CONFIGURATION_TYPE_EP, GoAppEngineRunConfigurationType.class);
  }
}