/*
 * Copyright 2013-2015 Sergey Ignatov, Alexander Zolotov, Florin Patan
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
import com.goide.runconfig.GoConfigurationFactoryBase;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationTypeBase;
import com.intellij.execution.configurations.ConfigurationTypeUtil;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.openapi.project.Project;
import com.intellij.util.ArrayUtil;
import org.jetbrains.annotations.NotNull;

public class GdbRunConfigurationType extends ConfigurationTypeBase {
  protected GdbRunConfigurationType() {
    super("GoGdbRunConfigurationType", "Go GDB", "Go GDB debug configuration", GoIcons.DEBUG);
    addFactory(new GoConfigurationFactoryBase(GdbRunConfigurationType.this) {
      @NotNull
      public RunConfiguration createTemplateConfiguration(Project project) {
        return new GdbRunConfiguration("", project, this);
      }

      @Override
      public boolean canConfigurationBeSingleton() {
        return false;
      }
    });
  }

  @NotNull
  public ConfigurationFactory getFactory() {
    ConfigurationFactory factory = ArrayUtil.getFirstElement(super.getConfigurationFactories());
    assert factory != null;
    return factory;
  }

  public static GdbRunConfigurationType getInstance() {
    return ConfigurationTypeUtil.findConfigurationType(GdbRunConfigurationType.class);
  }
}
