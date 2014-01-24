package com.goide.debugger.ideagdb.run;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.*;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.RunConfigurationWithSuppressedDefaultRunAction;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.DefaultJDOMExternalizer;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.WriteExternalException;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class GdbRunConfiguration extends ModuleBasedConfiguration<GdbRunConfigurationModule>
  implements RunConfigurationWithSuppressedDefaultRunAction,
             RunConfigurationWithSuppressedDefaultDebugAction {
  private static final Logger m_log =
    Logger.getInstance("#com.goide.debugger.ideagdb.run.GdbRunConfiguration");

  public String GDB_PATH = "gdb";
  public String APP_PATH = "";
  public String STARTUP_COMMANDS = "";

  public GdbRunConfiguration(String name, Project project, ConfigurationFactory factory) {
    super(name, new GdbRunConfigurationModule(project), factory);
  }

  @Override
  public Collection<Module> getValidModules() {
    m_log.warn("getValidModules: stub");
    return null;
  }

  @Override
  protected ModuleBasedConfiguration createInstance() {
    return new GdbRunConfiguration(getName(), getProject(), GdbRunConfigurationType.getInstance().getFactory());
  }

  @Override
  public SettingsEditor<? extends RunConfiguration> getConfigurationEditor() {
    return new GdbRunConfigurationEditor<GdbRunConfiguration>(getProject());
  }

  @Nullable
  @Override
  public RunProfileState getState(@NotNull Executor executor, @NotNull ExecutionEnvironment env)
    throws ExecutionException {
    return new GdbRunProfileState(this);
  }

  @Override
  public void readExternal(Element element) throws InvalidDataException {
    super.readExternal(element);
    readModule(element);
    DefaultJDOMExternalizer.readExternal(this, element);
  }

  @Override
  public void writeExternal(Element element) throws WriteExternalException {
    super.writeExternal(element);
    writeModule(element);
    DefaultJDOMExternalizer.writeExternal(this, element);
  }
}
