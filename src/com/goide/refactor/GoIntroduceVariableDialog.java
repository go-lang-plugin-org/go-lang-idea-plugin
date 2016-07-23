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

package com.goide.refactor;

import com.goide.GoFileType;
import com.goide.GoNamesValidator;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.refactoring.RefactoringBundle;
import com.intellij.refactoring.ui.NameSuggestionsField;
import com.intellij.refactoring.ui.RefactoringDialog;
import com.intellij.ui.IdeBorderFactory;
import com.intellij.util.ArrayUtil;
import com.intellij.util.ui.UIUtil;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

class GoIntroduceVariableDialog extends RefactoringDialog {
  private final GoIntroduceOperation myOperation;
  private final int myOccurrencesCount;
  private NameSuggestionsField myNameField;
  private JCheckBox myReplaceAllCheckBox;

  protected GoIntroduceVariableDialog(GoIntroduceOperation operation) {
    super(operation.getProject(), true);
    myOperation = operation;
    myOccurrencesCount = operation.getOccurrences().size();

    setTitle(RefactoringBundle.message("introduce.variable.title"));
    setModal(true);
    init();
  }

  @Override
  protected boolean hasHelpAction() {
    return false;
  }

  @Override
  protected boolean hasPreviewButton() {
    return false;
  }

  @Nullable
  @Override
  public JComponent getPreferredFocusedComponent() {
    return myNameField;
  }

  @Override
  protected void canRun() throws ConfigurationException {
    if (!areButtonsValid()) {
      throw new ConfigurationException(RefactoringBundle.message("refactoring.introduce.name.error"), getName());
    }
  }

  @Nullable
  @Override
  protected JComponent createCenterPanel() {
    JPanel panel = new JPanel(new BorderLayout());
    panel.add(createNamePanel(), BorderLayout.CENTER);
    if (myOccurrencesCount > 1) panel.add(createReplaceAllPanel(), BorderLayout.SOUTH);
    panel.setPreferredSize(new Dimension(myNameField.getWidth(), -1));
    return panel;
  }

  private JComponent createNamePanel() {
    JPanel panel = new JPanel(new BorderLayout());

    String[] names = ArrayUtil.toStringArray(myOperation.getSuggestedNames());
    myNameField = new NameSuggestionsField(names, myOperation.getProject(), GoFileType.INSTANCE);
    myNameField.setBorder(IdeBorderFactory.createEmptyBorder(3, 5, 2, 3));
    myNameField.addDataChangedListener(this::validateButtons);

    JLabel label = new JLabel(UIUtil.replaceMnemonicAmpersand(RefactoringBundle.message("name.prompt")));
    label.setLabelFor(myNameField);

    panel.add(myNameField, BorderLayout.CENTER);
    panel.add(label, BorderLayout.WEST);
    return panel;
  }

  private JComponent createReplaceAllPanel() {
    JPanel panel = new JPanel(new FlowLayout());

    String text = UIUtil.replaceMnemonicAmpersand(RefactoringBundle.message("replace.all.occurences", myOccurrencesCount));
    myReplaceAllCheckBox = new JCheckBox(text);
    panel.add(myReplaceAllCheckBox);

    return panel;
  }

  @Override
  protected boolean areButtonsValid() {
    return new GoNamesValidator().isIdentifier(getName(), myProject);
  }

  @Override
  protected void doAction() {
    closeOKAction();
  }

  public String getName() {
    return myNameField.getEnteredName();
  }

  public boolean getReplaceAll() {
    return myReplaceAllCheckBox != null && myReplaceAllCheckBox.isSelected();
  }
}
