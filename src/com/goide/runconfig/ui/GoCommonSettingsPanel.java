package com.goide.runconfig.ui;

import com.goide.runconfig.GoRunConfigurationBase;
import com.goide.util.GoUtil;
import com.intellij.application.options.ModulesComboBox;
import com.intellij.execution.configuration.EnvironmentVariablesTextFieldWithBrowseButton;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.ui.RawCommandLineEditor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class GoCommonSettingsPanel {
  private JPanel myPanel;
  private RawCommandLineEditor myParamsField;
  private TextFieldWithBrowseButton myWorkingDirectoryField;
  private EnvironmentVariablesTextFieldWithBrowseButton myEnvironmentField;
  private ModulesComboBox myModulesComboBox;

  public GoCommonSettingsPanel(@NotNull Project project) {
    GoUtil.installFileChooser(project, myWorkingDirectoryField, true);
  }

  public void resetEditorFrom(@NotNull GoRunConfigurationBase<?> configuration) {
    myModulesComboBox.setModules(configuration.getValidModules());
    myModulesComboBox.setSelectedModule(configuration.getConfigurationModule().getModule());
    myParamsField.setText(configuration.getParams());
    myWorkingDirectoryField.setText(configuration.getWorkingDirectory());
    myEnvironmentField.setEnvs(configuration.getCustomEnvironment());
    myEnvironmentField.setPassParentEnvs(configuration.isPassParentEnvironment());
  }

  public void applyEditorTo(@NotNull GoRunConfigurationBase<?> configuration) throws ConfigurationException {
    configuration.setModule(myModulesComboBox.getSelectedModule());
    configuration.setParams(myParamsField.getText());
    configuration.setWorkingDirectory(myWorkingDirectoryField.getText());
    configuration.setCustomEnvironment(myEnvironmentField.getEnvs());
    configuration.setPassParentEnvironment(myEnvironmentField.isPassParentEnvs());
  }

  @NotNull
  public JPanel getPanel() {
    return myPanel;
  }

  @Nullable
  public Module getSelectedModule() {
    return myModulesComboBox.getSelectedModule();
  }
}
