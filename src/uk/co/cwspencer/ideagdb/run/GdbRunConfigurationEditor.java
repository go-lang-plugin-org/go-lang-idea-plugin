package uk.co.cwspencer.ideagdb.run;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class GdbRunConfigurationEditor<T extends GdbRunConfiguration>
        extends SettingsEditor<T> {
    private static final Logger m_log =
            Logger.getInstance("#uk.co.cwspencer.ideagdb.run.GdbRunConfigurationEditor");

    private JPanel m_contentPanel;
    private TextFieldWithBrowseButton m_gdbPath;
    private TextFieldWithBrowseButton m_appPath;
    private JTextArea m_startupCommands;

    public GdbRunConfigurationEditor(final Project project) {
    }

    @Override
    protected void resetEditorFrom(T configuration) {
        m_gdbPath.setText(configuration.GDB_PATH);
        m_appPath.setText(configuration.APP_PATH);
        m_startupCommands.setText(configuration.STARTUP_COMMANDS);
    }

    @Override
    protected void applyEditorTo(T configuration) throws ConfigurationException {
        configuration.GDB_PATH = m_gdbPath.getText();
        configuration.APP_PATH = m_appPath.getText();
        configuration.STARTUP_COMMANDS = m_startupCommands.getText();
    }

    @NotNull
    @Override
    protected JComponent createEditor() {
        return m_contentPanel;
    }

    @Override
    protected void disposeEditor() {
    }
}
