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

import com.goide.appengine.YamlFilesModificationTracker;
import com.goide.runconfig.GoRunUtil;
import com.goide.runconfig.ui.GoCommonSettingsPanel;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.TextFieldWithHistoryWithBrowseButton;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class GoAppEngineRunConfigurationEditor extends SettingsEditor<GoAppEngineRunConfiguration> {
  private JPanel myComponent;
  private JBTextField myHostField;
  private JBTextField myPortField;
  private GoCommonSettingsPanel myCommonSettingsPanel;
  private JBTextField myAdminPortField;
  private TextFieldWithHistoryWithBrowseButton myConfigFileField;

  public GoAppEngineRunConfigurationEditor(@NotNull Project project) {
    super(null);
    initConfigFileField(project);
    myCommonSettingsPanel.init(project);
  }

  @Override
  protected void resetEditorFrom(@NotNull GoAppEngineRunConfiguration configuration) {
    myHostField.setText(StringUtil.notNullize(configuration.getHost()));
    myPortField.setText(StringUtil.notNullize(configuration.getPort()));
    myAdminPortField.setText(StringUtil.notNullize(configuration.getAdminPort()));
    myConfigFileField.getChildComponent().setText(StringUtil.notNullize(configuration.getConfigFile()));
    myCommonSettingsPanel.resetEditorFrom(configuration);
  }

  @Override
  protected void applyEditorTo(@NotNull GoAppEngineRunConfiguration configuration) throws ConfigurationException {
    configuration.setHost(StringUtil.nullize(myHostField.getText().trim()));
    configuration.setPort(StringUtil.nullize(myPortField.getText().trim()));
    configuration.setAdminPort(StringUtil.nullize(myAdminPortField.getText().trim()));
    configuration.setConfigFile(StringUtil.nullize(myConfigFileField.getText().trim()));
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

  private void initConfigFileField(@NotNull Project project) {
    GoRunUtil.installFileChooser(project, myConfigFileField, false, false, file -> "yaml".equals(file.getExtension()));
    myConfigFileField.getChildComponent().setHistory(ContainerUtil.map2List(
      YamlFilesModificationTracker.getYamlFiles(project, null), VirtualFile::getPath));
  }
}
