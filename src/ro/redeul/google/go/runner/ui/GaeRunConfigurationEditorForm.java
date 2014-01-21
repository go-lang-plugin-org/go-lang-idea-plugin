package ro.redeul.google.go.runner.ui;

import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.ui.RawCommandLineEditor;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.runner.GaeLocalConfiguration;

import javax.swing.*;
import java.io.File;

public class GaeRunConfigurationEditorForm extends SettingsEditor<GaeLocalConfiguration> {

    private DefaultComboBoxModel modulesModel;

    private JPanel component;
    private RawCommandLineEditor builderArguments;
    private TextFieldWithBrowseButton workingDirectoryBrowser;
    private RawCommandLineEditor envVars;
    private JTextField hostname;
    private JTextField port;
    private JTextField adminPort;

    @Override
    protected void resetEditorFrom(GaeLocalConfiguration configuration) {
        builderArguments.setText(configuration.builderArguments);
        workingDirectoryBrowser.setText(configuration.workingDir);
        if (workingDirectoryBrowser.getText().isEmpty()) {
            workingDirectoryBrowser.setText(configuration.getProject().getBasePath());
        }

        envVars.setText(configuration.envVars);
        hostname.setText(configuration.hostname);
        if (hostname.getText().isEmpty()) {
            hostname.setText("localhost");
        }

        port.setText(configuration.port);
        if (port.getText().isEmpty()) {
            port.setText("8080");
        }

        adminPort.setText(configuration.adminPort);
        if (adminPort.getText().isEmpty()) {
            adminPort.setText("8000");
        }
    }

    @Override
    protected void applyEditorTo(GaeLocalConfiguration configuration) throws ConfigurationException {
        String cwd = workingDirectoryBrowser.getText();

        if (cwd.isEmpty()) {
            throw new ConfigurationException("You must supply a working directory");
        }

        if (!(new File(cwd)).exists() || !(new File(cwd)).isDirectory()) {
            throw new ConfigurationException("You must supply a valid directory");
        }

        if (!(new File(cwd + File.separator + "app.yaml")).exists() || !(new File(cwd + File.separator + "app.yaml")).isFile()) {
            throw new ConfigurationException("You must supply a directory containing an app.yaml file");
        }

        if (hostname.getText().isEmpty()) {
            throw new ConfigurationException("hostname cannot be empty");
        }

        if (port.getText().isEmpty()) {
            throw new ConfigurationException("port cannot be empty");
        } else if (!port.getText().matches("\\d+")) {
            throw new ConfigurationException("port is not a valid number");
        } else if (Integer.parseInt(port.getText()) < 1024) {
            throw new ConfigurationException("port is below 1024 and you will need special privileges for it");
        }

        if (adminPort.getText().isEmpty()) {
            throw new ConfigurationException("admin_port cannot be empty");
        } else if (!adminPort.getText().matches("\\d+")) {
            throw new ConfigurationException("admin_port is not a valid number");
        } else if (Integer.parseInt(adminPort.getText()) < 1024) {
            throw new ConfigurationException("admin_port is below 1024 and you will need special privileges for it");
        }

        if (port.getText().equals(adminPort.getText())) {
            throw new ConfigurationException("port and admin_port must be different values");
        }

        // @TODO validation for app.yaml would be nice

        configuration.builderArguments = builderArguments.getText();
        configuration.workingDir = cwd;
        configuration.envVars = envVars.getText();
        configuration.hostname = hostname.getText();
        if (configuration.hostname.isEmpty()) {
            configuration.hostname = "localhost";
        }

        configuration.port = port.getText();
        if (configuration.port.isEmpty()) {
            configuration.port = "8080";
        }

        configuration.adminPort = adminPort.getText();
        if (configuration.adminPort.isEmpty()) {
            configuration.adminPort = "8000";
        }
    }

    public GaeRunConfigurationEditorForm(final Project project) {
        workingDirectoryBrowser.addBrowseFolderListener("Application working directory", "Application working directory",
                project, new FileChooserDescriptor(false, true, false, false, false, false));

        workingDirectoryBrowser.setText(project.getBasePath());
        hostname.setText("localhost");
        port.setText("8080");
        adminPort.setText("8000");
    }

    @NotNull
    @Override
    protected JComponent createEditor() {
        return component;
    }

    @Override
    protected void disposeEditor() {
        component.setVisible(false);
    }
}
