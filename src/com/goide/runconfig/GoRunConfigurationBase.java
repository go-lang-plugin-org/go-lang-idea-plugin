package com.goide.runconfig;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ModuleBasedConfiguration;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.RunConfigurationWithSuppressedDefaultRunAction;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.WriteExternalException;
import com.intellij.util.xmlb.XmlSerializer;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;

public abstract class GoRunConfigurationBase<RunningState extends GoRunningState> extends ModuleBasedConfiguration<GoModuleBasedConfiguration>
  implements RunConfigurationWithSuppressedDefaultRunAction {
  protected String myFilePath = "";
  protected String myParams = "";

  public GoRunConfigurationBase(String name, GoModuleBasedConfiguration configurationModule, ConfigurationFactory factory) {
    super(name, configurationModule, factory);
  }

  @Override
  public Collection<Module> getValidModules() {
    Module[] modules = ModuleManager.getInstance(getProject()).getModules();
    return Arrays.asList(modules);
  }

  @Override
  public void writeExternal(final Element element) throws WriteExternalException {
    super.writeExternal(element);
    writeModule(element);
    XmlSerializer.serializeInto(this, element);
  }

  @Override
  public void readExternal(final Element element) throws InvalidDataException {
    super.readExternal(element);
    readModule(element);
    XmlSerializer.deserializeInto(this, element);
  }

  public final RunningState createRunningState(ExecutionEnvironment env) throws ExecutionException {
    GoModuleBasedConfiguration configuration = getConfigurationModule();
    Module module = configuration.getModule();
    if (module == null) {
      throw new ExecutionException("Go isn't configured for run configuration: " + getName());
    }
    return newRunningState(env, module);
  }

  protected abstract RunningState newRunningState(ExecutionEnvironment env, Module module);

  @NotNull
  public String getParams() {
    return myParams;
  }

  public void setParams(@NotNull String params) {
    myParams = params;
  }

  @NotNull
  public String getFilePath() {
    return myFilePath;
  }

  public void setFilePath(@NotNull String filePath) {
    myFilePath = filePath;
  }
}
