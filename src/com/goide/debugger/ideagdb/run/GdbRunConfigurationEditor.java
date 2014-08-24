package com.goide.debugger.ideagdb.run;

import com.goide.util.GoUtil;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class GdbRunConfigurationEditor extends SettingsEditor<GdbRunConfiguration> {
  private JPanel myPanel;
  private TextFieldWithBrowseButton myGdbPath;
  private TextFieldWithBrowseButton myAppPath;
  private JTextArea myStartupCommands;

  public GdbRunConfigurationEditor(Project project) {
    GoUtil.installFileChooser(project, myAppPath, false);
    GoUtil.installFileChooser(project, myGdbPath, false);
  }

  @Override
  protected void resetEditorFrom(@NotNull GdbRunConfiguration configuration) {
    myGdbPath.setText(configuration.GDB_PATH);
    myAppPath.setText(configuration.APP_PATH);
    myStartupCommands.setText(configuration.STARTUP_COMMANDS);
  }

  @Override
  protected void applyEditorTo(@NotNull GdbRunConfiguration configuration) throws ConfigurationException {
    configuration.GDB_PATH = myGdbPath.getText();
    configuration.APP_PATH = myAppPath.getText();
    configuration.STARTUP_COMMANDS = myStartupCommands.getText();
  }

  @NotNull
  @Override
  protected JComponent createEditor() {
    return myPanel;
  }

  @Override
  protected void disposeEditor() {
  }
}
