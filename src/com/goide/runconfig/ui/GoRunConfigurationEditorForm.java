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

package com.goide.runconfig.ui;

import com.goide.GoModuleType;
import com.goide.runconfig.GoRunConfigurationWithMain;
import com.intellij.application.options.ModulesComboBox;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.TextBrowseFolderListener;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.ui.RawCommandLineEditor;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class GoRunConfigurationEditorForm extends SettingsEditor<GoRunConfigurationWithMain> {
  private JPanel component;
  private ModulesComboBox myComboModules;
  private RawCommandLineEditor myParamsField;
  private TextFieldWithBrowseButton myFilePathField;

  public GoRunConfigurationEditorForm(@NotNull Project project, boolean showPath) {
    super(null);
    FileChooserDescriptor chooseFileDescriptor = FileChooserDescriptorFactory.createSingleLocalFileDescriptor();
    chooseFileDescriptor.setRoots(project.getBaseDir());
    chooseFileDescriptor.setShowFileSystemRoots(false);
    myParamsField.setVisible(showPath);
    myFilePathField.addBrowseFolderListener(new TextBrowseFolderListener(chooseFileDescriptor));
  }

  @Override
  protected void resetEditorFrom(@NotNull GoRunConfigurationWithMain configuration) {
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
  protected void applyEditorTo(@NotNull GoRunConfigurationWithMain configuration) throws ConfigurationException {
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
