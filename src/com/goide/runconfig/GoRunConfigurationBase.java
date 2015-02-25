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

package com.goide.runconfig;

import com.goide.sdk.GoSdkService;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.configuration.EnvironmentVariablesComponent;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ModuleBasedConfiguration;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.RunConfigurationWithSuppressedDefaultRunAction;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.JDOMExternalizerUtil;
import com.intellij.openapi.util.WriteExternalException;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.util.containers.ContainerUtil;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Map;

public abstract class GoRunConfigurationBase<RunningState extends GoRunningState>
  extends ModuleBasedConfiguration<GoModuleBasedConfiguration> implements RunConfigurationWithSuppressedDefaultRunAction {

  private static final String PARAMETERS_NAME = "parameters";
  private static final String PASS_PARENT_ENV = "pass_parent_env";

  @NotNull private String myParams = "";
  @NotNull private Map<String, String> myCustomEnvironment = ContainerUtil.newHashMap();
  private boolean myPassParentEnvironment = true;

  public GoRunConfigurationBase(String name, GoModuleBasedConfiguration configurationModule, ConfigurationFactory factory) {
    super(name, configurationModule, factory);
    
    Module module = configurationModule.getModule();
    if (module == null) {
      Collection<Module> modules = getValidModules();
      if (modules.size() == 1) {
        getConfigurationModule().setModule(ContainerUtil.getFirstItem(modules));
      }
    }
  }

  @NotNull
  @Override
  public Collection<Module> getValidModules() {
    return ContainerUtil.filter(ModuleManager.getInstance(getProject()).getModules(), new Condition<Module>() {
      @Override
      public boolean value(Module module) {
        return GoSdkService.getInstance().isGoModule(module);
      }
    });
  }

  @Override
  public void writeExternal(final Element element) throws WriteExternalException {
    super.writeExternal(element);
    writeModule(element);
    if (StringUtil.isNotEmpty(myParams)) {
      JDOMExternalizerUtil.addElementWithValueAttribute(element, PARAMETERS_NAME, myParams);
    }
    if (!myCustomEnvironment.isEmpty()) {
      EnvironmentVariablesComponent.writeExternal(element, myCustomEnvironment);
    }
    if (!myPassParentEnvironment) {
      JDOMExternalizerUtil.addElementWithValueAttribute(element, PASS_PARENT_ENV, "false");
    }
  }

  @Override
  public void readExternal(@NotNull final Element element) throws InvalidDataException {
    super.readExternal(element);
    readModule(element);
    myParams = StringUtil.notNullize(JDOMExternalizerUtil.getFirstChildValueAttribute(element, PARAMETERS_NAME));
    EnvironmentVariablesComponent.readExternal(element, myCustomEnvironment);
    
    String passEnvValue = JDOMExternalizerUtil.getFirstChildValueAttribute(element, PASS_PARENT_ENV);
    myPassParentEnvironment = passEnvValue == null || Boolean.valueOf(passEnvValue);
  }

  @NotNull
  public final RunningState createRunningState(ExecutionEnvironment env) throws ExecutionException {
    GoModuleBasedConfiguration configuration = getConfigurationModule();
    Module module = configuration.getModule();
    if (module == null) {
      throw new ExecutionException("Go isn't configured for run configuration: " + getName());
    }
    return newRunningState(env, module);
  }

  @NotNull
  protected abstract RunningState newRunningState(ExecutionEnvironment env, Module module);

  @NotNull
  public String getParams() {
    return myParams;
  }

  public void setParams(@NotNull String params) {
    myParams = params;
  }
  
  @NotNull
  public Map<String, String> getCustomEnvironment() {
    return myCustomEnvironment;
  }

  public void setCustomEnvironment(@NotNull Map<String, String> customEnvironment) {
    myCustomEnvironment = customEnvironment;
  }

  public void setPassParentEnvironment(boolean passParentEnvironment) {
    myPassParentEnvironment = passParentEnvironment;
  }

  public boolean isPassParentEnvironment() {
    return myPassParentEnvironment;
  }

  public abstract String getWorkingDirectory();
}
