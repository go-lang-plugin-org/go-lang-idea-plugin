/*
 * Copyright 2013-2014 Sergey Ignatov, Alexander Zolotov, Mihai Toader
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

import com.goide.completion.GoCompletionUtil;
import com.goide.psi.GoFile;
import com.goide.runconfig.testing.GoTestRunConfiguration;
import com.goide.runconfig.ui.GoCommonSettingsPanel;
import com.goide.stubs.index.GoPackagesIndex;
import com.goide.util.GoUtil;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.ui.EditorTextField;
import com.intellij.ui.ListCellRendererWrapper;
import com.intellij.util.Processor;
import com.intellij.util.TextFieldCompletionProvider;
import org.intellij.lang.regexp.RegExpLanguage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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

  public GoTestRunConfigurationEditorForm(@NotNull final Project project) {
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
    myPackageField = new TextFieldCompletionProvider() {
      @Override
      protected void addCompletionVariants(@NotNull String text,
                                           int offset,
                                           @NotNull String prefix,
                                           @NotNull final CompletionResultSet result) {
        final Module module = myCommonSettingsPanel.getSelectedModule();
        if (module != null) {
          final GlobalSearchScope scope = GoUtil.moduleScope(module);
          for (String packageName : GoPackagesIndex.getAllPackages(myProject)) {
            StubIndex.getInstance().processElements(GoPackagesIndex.KEY, packageName, myProject, scope, GoFile.class,
                                                    new Processor<GoFile>() {
                                                      @Override
                                                      public boolean process(@NotNull GoFile file) {
                                                        String fullPackageName = file.getImportPath();
                                                        if (fullPackageName != null) {
                                                          result.addElement(GoCompletionUtil.createPackageLookupElement(fullPackageName, false));
                                                        }
                                                        return true;
                                                      }
                                                    });
          }
        }
      }
    }.createEditor(myProject);
  }

  @Nullable
  private static ListCellRendererWrapper<GoTestRunConfiguration.Kind> getTestKindListCellRendererWrapper() {
    return new ListCellRendererWrapper<GoTestRunConfiguration.Kind>() {
      @Override
      public void customize(JList list, @Nullable GoTestRunConfiguration.Kind kind, int index, boolean selected, boolean hasFocus) {
        if (kind != null) {
          String kindName = StringUtil.capitalize(kind.toString().toLowerCase());
          setText(kindName);
        }
      }
    };
  }

  private void installFileChoosers(@NotNull Project project) {
    GoUtil.installFileChooser(project, myFileField, false);
    GoUtil.installFileChooser(project, myDirectoryField, true);
  }

  private void installTestKindComboBox() {
    myTestKindComboBox.removeAllItems();
    myTestKindComboBox.setRenderer(getTestKindListCellRendererWrapper());
    for (GoTestRunConfiguration.Kind kind : GoTestRunConfiguration.Kind.values()) {
      myTestKindComboBox.addItem(kind);
    }
    myTestKindComboBox.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        onTestKindChanged();
      }
    });
  }
}
