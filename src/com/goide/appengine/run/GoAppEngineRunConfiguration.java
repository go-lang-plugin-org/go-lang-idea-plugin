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

import com.goide.runconfig.GoModuleBasedConfiguration;
import com.goide.runconfig.GoRunConfigurationBase;
import com.goide.sdk.GoSdkService;
import com.intellij.execution.configurations.*;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.JDOMExternalizerUtil;
import com.intellij.openapi.util.WriteExternalException;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.util.PathUtil;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GoAppEngineRunConfiguration extends GoRunConfigurationBase<GoAppEngineRunningState> {
  private static final String HOST_NAME = "HOST";
  private static final String PORT_NAME = "PORT";
  private static final String ADMIN_PORT_NAME = "ADMIN_PORT";
  private static final String CONFIG_FILE = "CONFIG";

  @Nullable private String myHost;
  @Nullable private String myPort;
  @Nullable private String myAdminPort;
  @Nullable private String myConfigFile;

  public GoAppEngineRunConfiguration(@NotNull Project project, String name, @NotNull ConfigurationType configurationType) {
    super(name, new GoModuleBasedConfiguration(project), configurationType.getConfigurationFactories()[0]);
  }

  @Nullable
  public String getHost() {
    return myHost;
  }

  public void setHost(@Nullable String host) {
    myHost = host;
  }

  @Nullable
  public String getPort() {
    return myPort;
  }

  public void setPort(@Nullable String port) {
    myPort = port;
  }
  
  @Nullable
  public String getAdminPort() {
    return myAdminPort;
  }

  public void setAdminPort(@Nullable String adminPort) {
    myAdminPort = adminPort;
  }
  
  @Nullable
  public String getConfigFile() {
    return myConfigFile;
  }

  public void setConfigFile(@Nullable String configFile) {
    myConfigFile = configFile;
  }

  @Override
  public void readExternal(@NotNull Element element) throws InvalidDataException {
    super.readExternal(element);
    myHost = JDOMExternalizerUtil.getFirstChildValueAttribute(element, HOST_NAME);
    myPort = JDOMExternalizerUtil.getFirstChildValueAttribute(element, PORT_NAME);
    myAdminPort = JDOMExternalizerUtil.getFirstChildValueAttribute(element, ADMIN_PORT_NAME);
    myConfigFile = JDOMExternalizerUtil.getFirstChildValueAttribute(element, CONFIG_FILE);
  }

  @Override
  public void writeExternal(Element element) throws WriteExternalException {
    super.writeExternal(element);
    addNonEmptyElement(element, HOST_NAME, myHost);
    addNonEmptyElement(element, PORT_NAME, myPort);
    addNonEmptyElement(element, ADMIN_PORT_NAME, myAdminPort);
    addNonEmptyElement(element, CONFIG_FILE, myConfigFile);
  }

  @Override
  public void checkConfiguration() throws RuntimeConfigurationException {
    super.checkConfiguration();

    Module module = getConfigurationModule().getModule();
    if (module != null) {
      if (!GoSdkService.getInstance(module.getProject()).isAppEngineSdk(module)) {
        throw new RuntimeConfigurationWarning("Go SDK is not specified for module '" + module.getName() + "'");
      }
    }

    checkPortValue(myPort, "Invalid port");
    checkPortValue(myAdminPort, "Invalid admin port");
    if (myConfigFile != null && !"yaml".equals(PathUtil.getFileExtension(myConfigFile))) {
      throw new RuntimeConfigurationException("Config file is not YAML");
    }
  }

  private static void checkPortValue(@Nullable String port, @NotNull String errorMessage) throws RuntimeConfigurationError {
    if (StringUtil.isNotEmpty(port)) {
      int intPort = StringUtil.parseInt(port, -1);
      if (intPort < 0 || intPort > Short.MAX_VALUE * 2) {
        throw new RuntimeConfigurationError(errorMessage);
      }
    }
  }

  @NotNull
  @Override
  public SettingsEditor<? extends RunConfiguration> getConfigurationEditor() {
    return new GoAppEngineRunConfigurationEditor(getProject());
  }

  @NotNull
  @Override
  protected GoAppEngineRunningState newRunningState(@NotNull ExecutionEnvironment env, @NotNull Module module) {
    return new GoAppEngineRunningState(env, module, this);
  }
}
