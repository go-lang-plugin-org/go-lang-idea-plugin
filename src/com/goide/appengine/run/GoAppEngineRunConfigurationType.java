/*
 * Copyright 2013-2016 Sergey Ignatov, Alexander Zolotov, Florin Patan
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
      @Override
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