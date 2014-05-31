package com.goide.runconfig.file;

import com.goide.GoIcons;
import com.intellij.compiler.options.CompileStepBeforeRun;
import com.intellij.execution.BeforeRunTask;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationTypeBase;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.openapi.extensions.Extensions;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import org.jetbrains.annotations.NotNull;

public class GoRunFileConfigurationType extends ConfigurationTypeBase {
  public GoRunFileConfigurationType() {
    super("GoRunFileConfiguration", "Go File", "Go run file configuration", GoIcons.ICON);
    addFactory(new ConfigurationFactory(this) {
      @NotNull
      public RunConfiguration createTemplateConfiguration(Project project) {
        return new GoRunFileConfiguration(project, "Go", getInstance());
      }

      @Override
      public void configureBeforeRunTaskDefaults(Key<? extends BeforeRunTask> providerID, @NotNull BeforeRunTask task) {
        if (providerID == CompileStepBeforeRun.ID) {
          task.setEnabled(false);
        }
      }
    });
  }

  @NotNull
  public static GoRunFileConfigurationType getInstance() {
    return Extensions.findExtension(CONFIGURATION_TYPE_EP, GoRunFileConfigurationType.class);
  }
}
