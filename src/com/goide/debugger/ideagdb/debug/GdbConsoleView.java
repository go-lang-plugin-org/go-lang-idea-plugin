package com.goide.debugger.ideagdb.debug;

import com.goide.debugger.gdb.Gdb;
import com.intellij.execution.impl.ConsoleViewImpl;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GdbConsoleView {
  private JPanel myContentPanel;
  private JTextField myPrompt;
  private JPanel myConsoleContainer;

  private final Gdb myGdb;

  // The actual console
  private final ConsoleViewImpl mConsole;

  // The last command that was sent
  private String myLastCommand;

  public GdbConsoleView(Gdb gdb, @NotNull Project project) {
    myGdb = gdb;
    mConsole = new ConsoleViewImpl(project, true);
    myConsoleContainer.add(mConsole.getComponent(), BorderLayout.CENTER);
    myPrompt.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(@NotNull ActionEvent event) {
        String command = event.getActionCommand();
        if (command.isEmpty() && myLastCommand != null) {
          // Resend the last command
          myGdb.sendCommand(myLastCommand);
        }
        else if (!command.isEmpty()) {
          // Send the command to GDB
          myLastCommand = command;
          myPrompt.setText("");
          myGdb.sendCommand(command);
        }
      }
    });
  }

  public ConsoleViewImpl getConsole() {
    return mConsole;
  }

  public JComponent getComponent() {
    return myContentPanel;
  }

  public JComponent getPreferredFocusableComponent() {
    return myPrompt;
  }
}
