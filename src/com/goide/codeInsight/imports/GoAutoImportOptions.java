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
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.ui.IdeBorderFactory;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class GoAutoImportOptions implements AutoImportOptionsProvider {
  private JPanel myPanel;
  private JCheckBox myCbAddUnambiguousImports;

  public GoAutoImportOptions() {
    FormBuilder builder = FormBuilder.createFormBuilder();
    myCbAddUnambiguousImports = new JCheckBox("Add unambiguous imports on the fly");
    builder.addComponent(myCbAddUnambiguousImports);
    myPanel = builder.getPanel();
    myPanel.setBorder(IdeBorderFactory.createTitledBorder("Go", true));
  }

  @Nullable
  @Override
  public JComponent createComponent() {
    return myPanel;
  }

  @Override
  public boolean isModified() {
    return GoCodeInsightSettings.getInstance().isAddUnambiguousImportsOnTheFly() != myCbAddUnambiguousImports.isSelected();
  }

  @Override
  public void apply() throws ConfigurationException {
    GoCodeInsightSettings.getInstance().setAddUnambiguousImportsOnTheFly(myCbAddUnambiguousImports.isSelected());
  }

  @Override
  public void reset() {
    myCbAddUnambiguousImports.setSelected(GoCodeInsightSettings.getInstance().isAddUnambiguousImportsOnTheFly());
  }

  @Override
  public void disposeUIResources() {
  }
}
