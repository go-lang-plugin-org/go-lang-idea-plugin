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

    @Override
    protected void resetEditorFrom(GaeLocalConfiguration configuration) {
        builderArguments.setText(configuration.builderArguments);
        workingDirectoryBrowser.setText(configuration.workingDir);
        if (workingDirectoryBrowser.getText().isEmpty()) {
            workingDirectoryBrowser.setText(configuration.getProject().getBasePath());
        }

        envVars.setText(configuration.envVars);
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

        // @TODO validation for app.yaml would be nice

        configuration.builderArguments = builderArguments.getText();
        configuration.workingDir = cwd;
        configuration.envVars = envVars.getText();
    }

    public GaeRunConfigurationEditorForm(final Project project) {
        workingDirectoryBrowser.addBrowseFolderListener("Application working directory", "Application working directory",
                project, new FileChooserDescriptor(false, true, false, false, false, false));

        workingDirectoryBrowser.setText(project.getBasePath());
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
