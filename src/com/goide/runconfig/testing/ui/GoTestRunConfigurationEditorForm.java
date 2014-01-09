package com.goide.runconfig.testing.ui;

import com.goide.GoModuleType;
import com.goide.runconfig.testing.GoTestRunConfiguration;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.roots.ui.configuration.ModulesCombobox;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.ui.EditorTextField;
import com.intellij.ui.RawCommandLineEditor;
import org.intellij.lang.regexp.RegExpLanguage;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class GoTestRunConfigurationEditorForm extends SettingsEditor<GoTestRunConfiguration> {
  private JPanel component;
  private ModulesCombobox myComboModules;
  private RawCommandLineEditor myParamsField;
  private EditorTextField myFilterEditor;
  private TextFieldWithBrowseButton myWorkingDirectoryField;

  public GoTestRunConfigurationEditorForm() {
    super(null);
  }

  @Override
  protected void resetEditorFrom(GoTestRunConfiguration configuration) {
    myComboModules.fillModules(configuration.getProject(), GoModuleType.getInstance());
    myComboModules.setSelectedModule(configuration.getConfigurationModule().getModule());
    myParamsField.setText(configuration.getParams());
    myFilterEditor.setText(configuration.getTestFilter());
    myWorkingDirectoryField.setText(configuration.getWorkingDirectory());
  }

  @Override
  protected void applyEditorTo(GoTestRunConfiguration configuration) throws ConfigurationException {
    configuration.setModule(myComboModules.getSelectedModule());
    configuration.setParams(myParamsField.getText());
    configuration.setTestFilter(myFilterEditor.getText());
    configuration.setWorkingDirectory(myWorkingDirectoryField.getText());
  }

  @NotNull
  @Override
  protected JComponent createEditor() {
    return component;
  }

  @Override
  protected void disposeEditor() {
    component.setVisible(false);
  }

  private void createUIComponents() {
    myFilterEditor = new EditorTextField("", null, RegExpLanguage.INSTANCE.getAssociatedFileType());
  }
}
