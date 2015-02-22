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
    myCommandTextField = TextFieldWithAutoCompletion.create(project, PREDEFINED_COMMANDS, true, null);
    myCommandTextField.setPreferredSize(new Dimension(250, 30));
    init();
  }

  @Nullable
  @Override
  protected ValidationInfo doValidate() {
    final String command = getCommand();
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
    final JPanel panel = new JPanel(new BorderLayout());
    panel.add(myCommandTextField, BorderLayout.CENTER);
    return panel;
  }

  @NotNull
  public String getCommand() {
    return myCommandTextField.getText().trim();
  }
}
