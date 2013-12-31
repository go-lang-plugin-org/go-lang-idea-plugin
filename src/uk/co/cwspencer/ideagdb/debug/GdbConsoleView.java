package uk.co.cwspencer.ideagdb.debug;

import com.intellij.execution.impl.ConsoleViewImpl;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import uk.co.cwspencer.gdb.Gdb;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Console tab for GDB input and output.
 */
public class GdbConsoleView {
    private static final Logger m_log =
            Logger.getInstance("#uk.co.cwspencer.ideagdb.debug.GdbConsoleView");

    private JPanel m_contentPanel;
    private JTextField m_prompt;
    private JPanel m_consoleContainer;

    private Gdb m_gdb;

    // The actual console
    private ConsoleViewImpl m_console;

    // The last command that was sent
    private String m_lastCommand;

    public GdbConsoleView(Gdb gdb, @NotNull Project project) {
        m_gdb = gdb;
        m_console = new ConsoleViewImpl(project, true);
        m_consoleContainer.add(m_console.getComponent(), BorderLayout.CENTER);
        m_prompt.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                String command = event.getActionCommand();
                if (command.isEmpty() && m_lastCommand != null) {
                    // Resend the last command
                    m_gdb.sendCommand(m_lastCommand);
                } else if (!command.isEmpty()) {
                    // Send the command to GDB
                    m_lastCommand = command;
                    m_prompt.setText("");
                    m_gdb.sendCommand(command);
                }
            }
        });
    }

    public ConsoleViewImpl getConsole() {
        return m_console;
    }

    public JComponent getComponent() {
        return m_contentPanel;
    }

    public JComponent getPreferredFocusableComponent() {
        return m_prompt;
    }
}
