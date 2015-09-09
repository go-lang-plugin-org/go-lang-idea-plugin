/*
 * Copyright 2013-2015 Sergey Ignatov, Alexander Zolotov, Florin Patan
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

package com.goide.runconfig.before;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.ui.TextFieldWithAutoCompletion;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.Collection;

public class GoCommandConfigureDialog extends DialogWrapper {
  private static final Collection<String> PREDEFINED_COMMANDS = Arrays.asList("vet", "test -i");

  private final TextFieldWithAutoCompletion<String> myCommandTextField;

  public GoCommandConfigureDialog(@NotNull Project project) {
    super(project);
    setTitle("Edit Go Command Task");
    setModal(true);
    myCommandTextField = TextFieldWithAutoCompletion.create(project, PREDEFINED_COMMANDS, false, null);
    init();
  }

  @Nullable
  @Override
  protected ValidationInfo doValidate() {
    String command = getCommand();
    if (command.isEmpty()) {
      return new ValidationInfo("Empty Go command is not allowed", myCommandTextField);
    }
    if (command.startsWith("go ")) {
      return new ValidationInfo("Go command shouldn't started with `go`", myCommandTextField);
    }
    return super.doValidate();
  }

  @Nullable
  @Override
  public JComponent getPreferredFocusedComponent() {
    return myCommandTextField;
  }

  @Nullable
  @Override
  protected JComponent createCenterPanel() {
    JPanel panel = new JPanel(new BorderLayout());
    panel.add(myCommandTextField, BorderLayout.NORTH);
    return panel;
  }

  @NotNull
  public String getCommand() {
    return myCommandTextField.getText().trim();
  }
}
