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

package com.goide.appengine.run;

import com.goide.runconfig.ui.GoCommonSettingsPanel;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.ui.components.JBTextField;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class GoAppEngineRunConfigurationEditor extends SettingsEditor<GoAppEngineRunConfiguration> {
  private JPanel myComponent;
  private JBTextField myHostField;
  private JBTextField myPortField;
  private GoCommonSettingsPanel myCommonSettingsPanel;

  public GoAppEngineRunConfigurationEditor(@NotNull final Project project) {
    super(null);
    myCommonSettingsPanel.init(project);
  }

  @Override
  protected void resetEditorFrom(@NotNull GoAppEngineRunConfiguration configuration) {
    myHostField.setText(StringUtil.notNullize(configuration.getHost()));
    myPortField.setText(StringUtil.notNullize(configuration.getPort()));
    myCommonSettingsPanel.resetEditorFrom(configuration);
  }

  @Override
  protected void applyEditorTo(@NotNull GoAppEngineRunConfiguration configuration) throws ConfigurationException {
    configuration.setHost(StringUtil.nullize(myHostField.getText().trim()));
    configuration.setPort(StringUtil.nullize(myPortField.getText().trim()));
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
