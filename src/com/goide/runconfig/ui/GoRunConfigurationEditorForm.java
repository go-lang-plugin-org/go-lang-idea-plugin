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

import com.goide.runconfig.GoRunConfigurationWithMain;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.TextBrowseFolderListener;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class GoRunConfigurationEditorForm extends SettingsEditor<GoRunConfigurationWithMain<?>> {
  private JPanel myComponent;
  private TextFieldWithBrowseButton myFilePathField;
  private GoCommonSettingsPanel myCommonSettingsPanel;


  public GoRunConfigurationEditorForm(@NotNull Project project) {
    super(null);
    myCommonSettingsPanel.init(project);
    FileChooserDescriptor chooseFileDescriptor = FileChooserDescriptorFactory.createSingleLocalFileDescriptor();
    chooseFileDescriptor.setRoots(project.getBaseDir());
    chooseFileDescriptor.setShowFileSystemRoots(false);
    myFilePathField.addBrowseFolderListener(new TextBrowseFolderListener(chooseFileDescriptor));
  }

  @Override
  protected void resetEditorFrom(@NotNull GoRunConfigurationWithMain<?> configuration) {
    myFilePathField.setText(configuration.getFilePath());
    myCommonSettingsPanel.resetEditorFrom(configuration);
  }

  @Override
  protected void applyEditorTo(@NotNull GoRunConfigurationWithMain<?> configuration) throws ConfigurationException {
    configuration.setFilePath(myFilePathField.getText());
    myCommonSettingsPanel.applyEditorTo(configuration);
  }

  @NotNull
  @Override
  protected JComponent createEditor() {
    return myComponent;
  }

  @Override
  protected void disposeEditor() {
    myComponent.setVisible(false);
  }
}
