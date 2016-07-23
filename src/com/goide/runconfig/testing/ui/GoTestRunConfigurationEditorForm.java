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

package com.goide.runconfig.testing.ui;

import com.goide.runconfig.GoRunUtil;
import com.goide.runconfig.testing.GoTestRunConfiguration;
import com.goide.runconfig.testing.frameworks.gobench.GobenchFramework;
import com.goide.runconfig.testing.frameworks.gocheck.GocheckFramework;
import com.goide.runconfig.testing.frameworks.gotest.GotestFramework;
import com.goide.runconfig.ui.GoCommonSettingsPanel;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.ui.EditorTextField;
import com.intellij.ui.ListCellRendererWrapper;
import org.intellij.lang.regexp.RegExpLanguage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Locale;

public class GoTestRunConfigurationEditorForm extends SettingsEditor<GoTestRunConfiguration> {
  @NotNull private final Project myProject;
  private JPanel myComponent;
  private EditorTextField myPatternEditor;

  private JComboBox myTestKindComboBox;
  private JLabel myFileLabel;
  private TextFieldWithBrowseButton myFileField;
  private JLabel myPackageLabel;
  private EditorTextField myPackageField;
  private JLabel myDirectoryLabel;
  private TextFieldWithBrowseButton myDirectoryField;
  private JLabel myPatternLabel;
  private GoCommonSettingsPanel myCommonSettingsPanel;
  private JRadioButton myGotestFrameworkRadioButton;
  private JRadioButton myGocheckFrameworkRadioButton;
  private JRadioButton myGobenchRadioButton;

  public GoTestRunConfigurationEditorForm(@NotNull Project project) {
    super(null);
    myProject = project;
    myCommonSettingsPanel.init(project);

    installTestKindComboBox();
    installFileChoosers(project);
  }

  private void onTestKindChanged() {
    GoTestRunConfiguration.Kind selectedKind = (GoTestRunConfiguration.Kind)myTestKindComboBox.getSelectedItem();
    if (selectedKind == null) {
      selectedKind = GoTestRunConfiguration.Kind.DIRECTORY;
    }
    boolean allInPackage = selectedKind == GoTestRunConfiguration.Kind.PACKAGE;
    boolean allInDirectory = selectedKind == GoTestRunConfiguration.Kind.DIRECTORY;
    boolean file = selectedKind == GoTestRunConfiguration.Kind.FILE;

    myPackageField.setVisible(allInPackage);
    myPackageLabel.setVisible(allInPackage);
    myDirectoryField.setVisible(allInDirectory);
    myDirectoryLabel.setVisible(allInDirectory);
    myFileField.setVisible(file);
    myFileLabel.setVisible(file);
    myPatternEditor.setVisible(!file);
    myPatternLabel.setVisible(!file);
  }

  @Override
  protected void resetEditorFrom(@NotNull GoTestRunConfiguration configuration) {
    myGotestFrameworkRadioButton.setSelected(configuration.getTestFramework() == GotestFramework.INSTANCE);
    myGocheckFrameworkRadioButton.setSelected(configuration.getTestFramework() == GocheckFramework.INSTANCE);
    myGobenchRadioButton.setSelected(configuration.getTestFramework() == GobenchFramework.INSTANCE);
    myTestKindComboBox.setSelectedItem(configuration.getKind());
    myPackageField.setText(configuration.getPackage());

    String directoryPath = configuration.getDirectoryPath();
    myDirectoryField.setText(directoryPath.isEmpty() ? configuration.getProject().getBasePath() : directoryPath);

    String filePath = configuration.getFilePath();
    myFileField.setText(filePath.isEmpty() ? configuration.getProject().getBasePath() : filePath);

    myPatternEditor.setText(configuration.getPattern());

    myCommonSettingsPanel.resetEditorFrom(configuration);
  }

  @Override
  protected void applyEditorTo(@NotNull GoTestRunConfiguration configuration) throws ConfigurationException {
    if (myGocheckFrameworkRadioButton.isSelected()) {
      configuration.setTestFramework(GocheckFramework.INSTANCE);
    } else if (myGobenchRadioButton.isSelected()) {
      configuration.setTestFramework(GobenchFramework.INSTANCE);
    } else {
      configuration.setTestFramework(GotestFramework.INSTANCE);
    }
    configuration.setKind((GoTestRunConfiguration.Kind)myTestKindComboBox.getSelectedItem());
    configuration.setPackage(myPackageField.getText());
    configuration.setDirectoryPath(myDirectoryField.getText());
    configuration.setFilePath(myFileField.getText());
    configuration.setPattern(myPatternEditor.getText());

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

  private void createUIComponents() {
    myPatternEditor = new EditorTextField("", null, RegExpLanguage.INSTANCE.getAssociatedFileType());
    myPackageField = new GoPackageFieldCompletionProvider(
      () -> myCommonSettingsPanel != null ? myCommonSettingsPanel.getSelectedModule() : null).createEditor(myProject);
  }

  @Nullable
  private static ListCellRendererWrapper<GoTestRunConfiguration.Kind> getTestKindListCellRendererWrapper() {
    return new ListCellRendererWrapper<GoTestRunConfiguration.Kind>() {
      @Override
      public void customize(JList list, @Nullable GoTestRunConfiguration.Kind kind, int index, boolean selected, boolean hasFocus) {
        if (kind != null) {
          String kindName = StringUtil.capitalize(kind.toString().toLowerCase(Locale.US));
          setText(kindName);
        }
      }
    };
  }

  private void installFileChoosers(@NotNull Project project) {
    GoRunUtil.installFileChooser(project, myFileField, false);
    GoRunUtil.installFileChooser(project, myDirectoryField, true);
  }

  private void installTestKindComboBox() {
    myTestKindComboBox.removeAllItems();
    myTestKindComboBox.setRenderer(getTestKindListCellRendererWrapper());
    for (GoTestRunConfiguration.Kind kind : GoTestRunConfiguration.Kind.values()) {
      myTestKindComboBox.addItem(kind);
    }
    myTestKindComboBox.addActionListener(e -> onTestKindChanged());
  }
}
