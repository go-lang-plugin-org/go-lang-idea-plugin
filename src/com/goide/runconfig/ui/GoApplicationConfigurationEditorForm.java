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

package com.goide.runconfig.ui;

import com.goide.runconfig.GoRunUtil;
import com.goide.runconfig.application.GoApplicationConfiguration;
import com.goide.runconfig.testing.ui.GoPackageFieldCompletionProvider;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.ui.EditorTextField;
import com.intellij.ui.ListCellRendererWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Locale;

public class GoApplicationConfigurationEditorForm extends SettingsEditor<GoApplicationConfiguration> {
  @NotNull private final Project myProject;
  private JPanel myComponent;
  private TextFieldWithBrowseButton myFileField;
  private GoCommonSettingsPanel myCommonSettingsPanel;
  private EditorTextField myPackageField;
  private JComboBox myRunKindComboBox;
  private JLabel myPackageLabel;
  private JLabel myFileLabel;
  private TextFieldWithBrowseButton myOutputFilePathField;


  public GoApplicationConfigurationEditorForm(@NotNull Project project) {
    super(null);
    myProject = project;
    myCommonSettingsPanel.init(project);

    installRunKindComboBox();
    GoRunUtil.installGoWithMainFileChooser(myProject, myFileField);
    GoRunUtil.installFileChooser(myProject, myOutputFilePathField, true, true);
  }

  private void onRunKindChanged() {
    GoApplicationConfiguration.Kind selectedKind = (GoApplicationConfiguration.Kind)myRunKindComboBox.getSelectedItem();
    if (selectedKind == null) {
      selectedKind = GoApplicationConfiguration.Kind.PACKAGE;
    }
    boolean thePackage = selectedKind == GoApplicationConfiguration.Kind.PACKAGE;
    boolean file = selectedKind == GoApplicationConfiguration.Kind.FILE;

    myPackageField.setVisible(thePackage);
    myPackageLabel.setVisible(thePackage);
    myFileField.setVisible(file);
    myFileLabel.setVisible(file);
  }

  @Override
  protected void resetEditorFrom(@NotNull GoApplicationConfiguration configuration) {
    myFileField.setText(configuration.getFilePath());
    myPackageField.setText(configuration.getPackage());
    myRunKindComboBox.setSelectedItem(configuration.getKind());
    myOutputFilePathField.setText(StringUtil.notNullize(configuration.getOutputFilePath()));
    myCommonSettingsPanel.resetEditorFrom(configuration);
  }

  @Override
  protected void applyEditorTo(@NotNull GoApplicationConfiguration configuration) throws ConfigurationException {
    configuration.setFilePath(myFileField.getText());
    configuration.setPackage(myPackageField.getText());
    configuration.setKind((GoApplicationConfiguration.Kind)myRunKindComboBox.getSelectedItem());
    configuration.setFileOutputPath(StringUtil.nullize(myOutputFilePathField.getText()));
    myCommonSettingsPanel.applyEditorTo(configuration);
  }

  private void createUIComponents() {
    myPackageField = new GoPackageFieldCompletionProvider(
      () -> myCommonSettingsPanel != null ? myCommonSettingsPanel.getSelectedModule() : null).createEditor(myProject);
  }

  @Nullable
  private static ListCellRendererWrapper<GoApplicationConfiguration.Kind> getRunKindListCellRendererWrapper() {
    return new ListCellRendererWrapper<GoApplicationConfiguration.Kind>() {
      @Override
      public void customize(JList list, @Nullable GoApplicationConfiguration.Kind kind, int index, boolean selected, boolean hasFocus) {
        if (kind != null) {
          String kindName = StringUtil.capitalize(kind.toString().toLowerCase(Locale.US));
          setText(kindName);
        }
      }
    };
  }

  private void installRunKindComboBox() {
    myRunKindComboBox.removeAllItems();
    myRunKindComboBox.setRenderer(getRunKindListCellRendererWrapper());
    for (GoApplicationConfiguration.Kind kind : GoApplicationConfiguration.Kind.values()) {
      myRunKindComboBox.addItem(kind);
    }
    myRunKindComboBox.addActionListener(e -> onRunKindChanged());
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
