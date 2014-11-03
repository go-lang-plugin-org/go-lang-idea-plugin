/*
 * Copyright 2013-2014 Sergey Ignatov, Alexander Zolotov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
    @NotNull
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

  @NotNull
  @Override
  public String getDisplayName() {
    return "Go GDB";
  }

  @NotNull
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

  @NotNull
  @Override
  public ConfigurationFactory[] getConfigurationFactories() {
    return new ConfigurationFactory[]{myFactory};
  }

  @NotNull
  public ConfigurationFactory getFactory() {
    return myFactory;
  }
}
