package com.goide.runconfig.application;

import com.goide.psi.GoFile;
import com.goide.psi.impl.GoFunctionDeclarationImpl;
import com.goide.psi.impl.GoPsiImplUtil;
import com.goide.runconfig.GoModuleBasedConfiguration;
import com.goide.runconfig.GoRunConfigurationBase;
import com.goide.runconfig.GoRunner;
import com.goide.runconfig.application.ui.GoRunConfigurationEditorForm;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.*;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class GoApplicationConfiguration extends GoRunConfigurationBase<GoApplicationRunningState> {
  private String myParams = "";
  private String myFilePath = "";

  public GoApplicationConfiguration(Project project, String name, ConfigurationType configurationType) {
    super(name, new GoModuleBasedConfiguration(project), configurationType.getConfigurationFactories()[0]);
  }

  @Override
  protected ModuleBasedConfiguration createInstance() {
    return new GoApplicationConfiguration(getProject(), getName(), GoApplicationRunConfigurationType.getInstance());
  }

  @NotNull
  @Override
  public SettingsEditor<? extends RunConfiguration> getConfigurationEditor() {
    return new GoRunConfigurationEditorForm(getProject());
  }

  @Override
  public RunProfileState getState(@NotNull Executor executor, @NotNull ExecutionEnvironment env) throws ExecutionException {
    return GoRunner.EMPTY_RUN_STATE;
  }

  @Override
  protected GoApplicationRunningState newRunningState(ExecutionEnvironment env, Module module) {
    return new GoApplicationRunningState(env, module, this);
  }

  @Override
  public void checkConfiguration() throws RuntimeConfigurationException {
    GoModuleBasedConfiguration configurationModule = getConfigurationModule();
    configurationModule.checkForWarning();

    Module module = configurationModule.getModule();
    if (module == null) return;
    VirtualFile file = VfsUtil.findFileByIoFile(new File(myFilePath), false);
    if (file == null) throw new RuntimeConfigurationError("Main file is not specified");
    PsiFile psiFile = PsiManager.getInstance(getProject()).findFile(file);
    if (psiFile == null || !(psiFile instanceof GoFile)) {
      throw new RuntimeConfigurationError("Main file is invalid");
    }
    if (!"main".equals(GoPsiImplUtil.getPackageName((GoFile)psiFile))) {
      throw new RuntimeConfigurationError("Main file has non-main package");
    }
    GoFunctionDeclarationImpl mainFunction = GoPsiImplUtil.findMainFunction((GoFile)psiFile);
    if (mainFunction == null) {
      throw new RuntimeConfigurationError("Main file doesn't contain main function");
    }
  }

  @Override
  public boolean isTestRunConfiguration() {
    return false;
  }

  @NotNull
  public String getParams() {
    return myParams;
  }

  public void setParams(@NotNull String params) {
    this.myParams = params;
  }

  @NotNull
  public String getFilePath() {
    return myFilePath;
  }

  public void setFilePath(@NotNull String filePath) {
    myFilePath = filePath;
  }
}
