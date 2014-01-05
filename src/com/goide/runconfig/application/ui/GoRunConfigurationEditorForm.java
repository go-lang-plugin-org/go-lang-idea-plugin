package com.goide.runconfig.application.ui;

import com.goide.GoModuleType;
import com.goide.runconfig.application.GoApplicationConfiguration;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ui.configuration.ModulesCombobox;
import com.intellij.openapi.ui.TextBrowseFolderListener;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.util.Factory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class GoRunConfigurationEditorForm extends SettingsEditor<GoApplicationConfiguration> {
  private JPanel component;
  private ModulesCombobox myComboModules;
  private JTextField myParamsField;
  private TextFieldWithBrowseButton myFilePathField;

  public GoRunConfigurationEditorForm(@NotNull Project project) {
    this(project, null);
  }

  public GoRunConfigurationEditorForm(@NotNull Project project, @Nullable Factory<GoApplicationConfiguration> factory) {
    super(factory);
    FileChooserDescriptor chooseFileDescriptor = FileChooserDescriptorFactory.createSingleLocalFileDescriptor();
    chooseFileDescriptor.setRoots(project.getBaseDir());
    chooseFileDescriptor.setShowFileSystemRoots(false);
    myFilePathField.addBrowseFolderListener(new TextBrowseFolderListener(chooseFileDescriptor));
  }

  @Override
  protected void resetEditorFrom(GoApplicationConfiguration configuration) {
    myComboModules.fillModules(configuration.getProject(), GoModuleType.getInstance());
    myComboModules.setSelectedModule(configuration.getConfigurationModule().getModule());
    myParamsField.setText(configuration.getParams());
    String filepath = configuration.getFilePath();
    if (filepath.isEmpty()) {
      filepath = configuration.getProject().getBasePath();
    }
    myFilePathField.setText(filepath);
  }

  @Override
  protected void applyEditorTo(GoApplicationConfiguration configuration) throws ConfigurationException {
    configuration.setModule(myComboModules.getSelectedModule());
    configuration.setParams(myParamsField.getText());
    configuration.setFilePath(myFilePathField.getText());
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
}
