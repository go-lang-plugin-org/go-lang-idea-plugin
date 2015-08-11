/*
 * Copyright 2013-2015 Sergey Ignatov, Alexander Zolotov, Mihai Toader, Florin Patan
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

package com.goide.codeInsight.imports;

import com.intellij.application.options.editor.AutoImportOptionsProvider;
import com.intellij.openapi.application.ApplicationBundle;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.ui.*;
import com.intellij.ui.components.JBList;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class GoAutoImportOptions implements AutoImportOptionsProvider {
  private JPanel myPanel;
  private JCheckBox myCbShowImportPopup;
  private JCheckBox myCbAddUnambiguousImports;
  private GoCodeInsightSettings mySettings;
  private JBList myExcludePackagesList;
  private DefaultListModel myExcludePackagesModel;

  public GoAutoImportOptions(@NotNull GoCodeInsightSettings settings) {
    mySettings = settings;
    FormBuilder builder = FormBuilder.createFormBuilder();
    myCbShowImportPopup = new JCheckBox(ApplicationBundle.message("checkbox.show.import.popup"));
    myCbAddUnambiguousImports = new JCheckBox(ApplicationBundle.message("checkbox.add.unambiguous.imports.on.the.fly"));
    builder.addComponent(myCbShowImportPopup);
    builder.addComponent(myCbAddUnambiguousImports);
    builder.addComponent(createExcludePackagesPanel());
    myPanel = builder.getPanel();
    myPanel.setBorder(IdeBorderFactory.createTitledBorder("Go", true));
  }

  private JPanel createExcludePackagesPanel() {
    JPanel panel = new JPanel(new BorderLayout());
    panel.setBorder(IdeBorderFactory.createTitledBorder(ApplicationBundle.message("exclude.from.completion.group"), true));
    myExcludePackagesList = new JBList();
    panel.add(ToolbarDecorator.createDecorator(myExcludePackagesList)
                .setAddAction(new AnActionButtonRunnable() {
                  @Override
                  public void run(AnActionButton button) {
                    String packageName = Messages.showInputDialog(myPanel,
                                                                  "Enter the import path to exclude from auto-import and completion:",
                                                                  ApplicationBundle.message("exclude.from.completion.title"),
                                                                  Messages.getWarningIcon(), "", null);
                    addExcludePackage(packageName);
                  }
                }).disableUpDownActions().createPanel(), BorderLayout.CENTER);
    return panel;
  }

  private void addExcludePackage(@Nullable String packageName) {
    if (StringUtil.isEmpty(packageName)) return;
    int index = - Arrays.binarySearch(myExcludePackagesModel.toArray(), packageName) - 1;
    if (index >= 0) {
      myExcludePackagesModel.add(index, packageName);
      ListScrollingUtil.ensureIndexIsVisible(myExcludePackagesList, index, 0);
    }
    myExcludePackagesList.clearSelection();
    myExcludePackagesList.setSelectedValue(packageName, true);
  }

  private String[] getExcludedPackages() {
    String[] excludedPackages = new String[myExcludePackagesModel.size()];
    for (int i = 0; i < myExcludePackagesModel.size(); i++) {
      excludedPackages[i] = (String)myExcludePackagesModel.elementAt(i);
    }
    Arrays.sort(excludedPackages);
    return excludedPackages;
  }

  @Nullable
  @Override
  public JComponent createComponent() {
    return myPanel;
  }

  @Override
  public boolean isModified() {
    return mySettings.isShowImportPopup() != myCbShowImportPopup.isSelected() ||
           mySettings.isAddUnambiguousImportsOnTheFly() != myCbAddUnambiguousImports.isSelected() ||
           !Arrays.equals(getExcludedPackages(), GoCodeInsightSettings.getInstance().getExcludedPackages());
  }

  @Override
  public void apply() throws ConfigurationException {
    mySettings.setShowImportPopup(myCbShowImportPopup.isSelected());
    mySettings.setAddUnambiguousImportsOnTheFly(myCbAddUnambiguousImports.isSelected());
    mySettings.setExcludedPackages(getExcludedPackages());
  }

  @Override
  public void reset() {
    myCbShowImportPopup.setSelected(mySettings.isShowImportPopup());
    myCbAddUnambiguousImports.setSelected(mySettings.isAddUnambiguousImportsOnTheFly());

    myExcludePackagesModel = new DefaultListModel();
    for (String name : GoCodeInsightSettings.getInstance().getExcludedPackages()) {
      myExcludePackagesModel.add(myExcludePackagesModel.size(), name);
    }
    myExcludePackagesList.setModel(myExcludePackagesModel);
  }

  @Override
  public void disposeUIResources() {
  }
}
