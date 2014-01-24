package com.goide.debugger.ideagdb.run;

import com.goide.GoIcons;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationType;
import com.intellij.execution.configurations.ConfigurationTypeUtil;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class GdbRunConfigurationType implements ConfigurationType {
  private final ConfigurationFactory myFactory = new ConfigurationFactory(this) {
    public RunConfiguration createTemplateConfiguration(Project project) {
      return new GdbRunConfiguration("", project, this);
    }

    @Override
    public boolean canConfigurationBeSingleton() {
      return false;
    }
  };

  public static GdbRunConfigurationType getInstance() {
    return ConfigurationTypeUtil.findConfigurationType(GdbRunConfigurationType.class);
  }

  @Override
  public String getDisplayName() {
    return "Go GDB";
  }

  @Override
  public String getConfigurationTypeDescription() {
    return "Go GDB debug configuration";
  }

  @Override
  public Icon getIcon() {
    return GoIcons.DEBUG;
  }

  @NotNull
  @Override
  public String getId() {
    return "GoGdbRunConfigurationType";
  }

  @Override
  public ConfigurationFactory[] getConfigurationFactories() {
    return new ConfigurationFactory[]{myFactory};
  }

  public ConfigurationFactory getFactory() {
    return myFactory;
  }
}
