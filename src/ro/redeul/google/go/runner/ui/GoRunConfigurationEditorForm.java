package ro.redeul.google.go.runner.ui;

import com.intellij.execution.ui.ConfigurationModuleSelector;
import com.intellij.ide.util.TreeFileChooser;
import com.intellij.ide.util.TreeFileChooserFactory;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.ui.RawCommandLineEditor;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.GoFileType;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.runner.GoApplicationConfiguration;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by IntelliJ IDEA.
 * User: mtoader
 * Date: Aug 19, 2010
 * Time: 3:00:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class GoRunConfigurationEditorForm extends SettingsEditor<GoApplicationConfiguration> {

    private DefaultComboBoxModel modulesModel;

    private JPanel component;
    private RawCommandLineEditor appArguments;
    private TextFieldWithBrowseButton applicationName;
    private JComboBox comboModules;

    private ConfigurationModuleSelector moduleSelector;
    private Project project;

    @Override
    protected void resetEditorFrom(GoApplicationConfiguration configuration) {
        applicationName.setText(configuration.scriptName);
        appArguments.setText(configuration.scriptArguments);

        moduleSelector.reset(configuration);
    }

    @Override
    protected void applyEditorTo(GoApplicationConfiguration configuration) throws ConfigurationException {
        configuration.scriptName = applicationName.getText();
        configuration.scriptArguments = appArguments.getText();
        moduleSelector.applyTo(configuration);
    }

    public GoRunConfigurationEditorForm(final Project project) {

        this.project = project;

        applicationName.getButton().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                TreeFileChooser fileChooser = TreeFileChooserFactory.getInstance(project).createFileChooser("Go Application Chooser", null,
                        GoFileType.GO_FILE_TYPE,
                        new TreeFileChooser.PsiFileFilter() {
                            public boolean accept(PsiFile file) {

                                if (!(file instanceof GoFile)) {
                                    return false;
                                }

                                GoFile goFile = (GoFile) file;

                                return goFile.getPackage().isMainPackage();
                            }
                        }, true, false);

                fileChooser.showDialog();


                PsiFile selectedFile = fileChooser.getSelectedFile();
                if ( selectedFile != null ) {
                    setChosenFile(selectedFile.getVirtualFile());
                }
            }
        });
    }

    private void setChosenFile(VirtualFile virtualFile) {
        applicationName.setText(virtualFile.getPath());
    }

    @NotNull
    @Override
    protected JComponent createEditor() {
        moduleSelector = new ConfigurationModuleSelector(project, comboModules);
        return component;
    }

    @Override
    protected void disposeEditor() {
    }

}
