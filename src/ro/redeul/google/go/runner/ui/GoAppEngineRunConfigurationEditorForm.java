package ro.redeul.google.go.runner.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.RawCommandLineEditor;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.runner.GoAppEngineApplicationConfiguration;

/**
 * Created by IntelliJ IDEA.
 * User: jhonny
 * Date: Aug 19, 2010
 * Time: 3:00:32 PM
 */
public class GoAppEngineRunConfigurationEditorForm
    extends SettingsEditor<GoAppEngineApplicationConfiguration> {

    private DefaultComboBoxModel modulesModel;

    private JPanel component;
    private RawCommandLineEditor appArguments;
    private TextFieldWithBrowseButton sdkDirectory;
    private JTextField email;
    private JPasswordField password;

    @Override
    protected void resetEditorFrom(GoAppEngineApplicationConfiguration configuration) {
        sdkDirectory.setText(configuration.sdkDirectory);
        password.setText(configuration.password);
        email.setText(configuration.email);
        appArguments.setText(configuration.scriptArguments);
    }

    @Override
    protected void applyEditorTo(GoAppEngineApplicationConfiguration configuration)
        throws ConfigurationException {
        configuration.sdkDirectory = sdkDirectory.getText();
        configuration.email = email.getText();
        configuration.password = new String(password.getPassword());
        configuration.scriptArguments = appArguments.getText();
    }

    public GoAppEngineRunConfigurationEditorForm(final Project project) {
        sdkDirectory.getButton().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                VirtualFile[] files = FileChooser.chooseFiles(
                    FileChooserDescriptorFactory.createSingleFolderDescriptor(),
                    project,
                    null);

                if (files.length == 1) {
                    sdkDirectory.setText(files[0].getPath());
                }
            }
        });
    }

    @NotNull
    @Override
    protected JComponent createEditor() {
        return component;
    }

    @Override
    protected void disposeEditor() {
    }

}
