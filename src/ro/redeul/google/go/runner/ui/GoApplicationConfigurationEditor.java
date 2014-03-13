package ro.redeul.google.go.runner.ui;

import com.intellij.ide.util.TreeFileChooser;
import com.intellij.ide.util.TreeFileChooserFactory;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
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
import uk.co.cwspencer.ideagdb.debug.go.GoGdbUtil;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GoApplicationConfigurationEditor extends SettingsEditor<GoApplicationConfiguration> {

    private DefaultComboBoxModel modulesModel;

    private JPanel component;
    private RawCommandLineEditor appArguments;
    private TextFieldWithBrowseButton applicationName;
    private JCheckBox buildBeforeRunCheckBox;
    private TextFieldWithBrowseButton buildDirectoryPathBrowser;
    private RawCommandLineEditor m_debugBuilderArguments;
    private TextFieldWithBrowseButton workingDirectoryBrowser;
    private RawCommandLineEditor envVars;
	private JTabbedPane tabbedPane1;
	private JLabel gdbVersionWarning;
	private TextFieldWithBrowseButton m_gdbPath;
	private JCheckBox autoStartGdb;
	private JTextArea m_startupCommands;
	private RawCommandLineEditor m_runBuilderArguments;

	@Override
    protected void resetEditorFrom(GoApplicationConfiguration configuration) {
        applicationName.setText(configuration.scriptName);
        appArguments.setText(configuration.scriptArguments);
        m_runBuilderArguments.setText(configuration.runBuilderArguments);
        buildBeforeRunCheckBox.setSelected(configuration.goBuildBeforeRun);
        buildDirectoryPathBrowser.setEnabled(configuration.goBuildBeforeRun);
        buildDirectoryPathBrowser.setText(configuration.goOutputDir);
        workingDirectoryBrowser.setText(configuration.workingDir);
        if (workingDirectoryBrowser.getText().isEmpty()) {
            workingDirectoryBrowser.setText(configuration.getProject().getBasePath());
        }

        envVars.setText(configuration.envVars);

		//Debug stuff
        if (configuration.debugBuilderArguments.isEmpty()) {
            configuration.debugBuilderArguments = "-gcflags \"-N -l\"";
        }
        m_debugBuilderArguments.setText(configuration.debugBuilderArguments);
        m_gdbPath.setText(configuration.GDB_PATH);
        m_startupCommands.setText(configuration.STARTUP_COMMANDS);
        autoStartGdb.setSelected(configuration.autoStartGdb);
    }

    @Override
    protected void applyEditorTo(GoApplicationConfiguration configuration) throws ConfigurationException {
        if (applicationName.getText().length() == 0)
            throw new ConfigurationException("Please select the file to run.");
        if (buildBeforeRunCheckBox.isSelected() && buildDirectoryPathBrowser.getText().equals("")) {
            throw new ConfigurationException("Please select the directory for the executable.");
        }

        configuration.scriptName = applicationName.getText();
        configuration.scriptArguments = appArguments.getText();
        configuration.runBuilderArguments = m_runBuilderArguments.getText();
        configuration.goBuildBeforeRun = buildBeforeRunCheckBox.isSelected();
        configuration.goOutputDir = buildDirectoryPathBrowser.getText();
        configuration.workingDir = workingDirectoryBrowser.getText();
        configuration.envVars = envVars.getText();

        //Debug stuff
        String gdbPath = m_gdbPath.getText();
        if (gdbPath.isEmpty()) {
            throw new ConfigurationException("Please select the path to gdb.");
        } else if (!GoGdbUtil.doesExecutableExist(gdbPath) || !GoGdbUtil.isValidGdbPath(gdbPath)){
            throw new ConfigurationException("Please select a valid path to gdb.");
        } else {
            gdbVersionWarning.setVisible(!GoGdbUtil.isKnownGdb(gdbPath));
        }

        configuration.GDB_PATH = m_gdbPath.getText();
        configuration.STARTUP_COMMANDS = m_startupCommands.getText();
        configuration.autoStartGdb = autoStartGdb.isSelected();
        configuration.debugBuilderArguments = m_debugBuilderArguments.getText();
    }

    public GoApplicationConfigurationEditor(final Project project) {

        applicationName.getButton().addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {

                        TreeFileChooser fileChooser =
                                TreeFileChooserFactory.getInstance(project).createFileChooser(
                                        "Go Application Chooser", null,
                                        GoFileType.INSTANCE,
                                        new TreeFileChooser.PsiFileFilter() {
                                            public boolean accept(PsiFile file) {

                                                if (!(file instanceof GoFile)) {
                                                    return false;
                                                }

                                                return ((GoFile) file).getMainFunction() != null;
                                            }
                                        }, true, false);

                        fileChooser.showDialog();

                        PsiFile selectedFile = fileChooser.getSelectedFile();
                        if (selectedFile != null) {
                            setChosenFile(selectedFile.getVirtualFile());
                        }
                    }
                });

        buildDirectoryPathBrowser.addBrowseFolderListener("Go executable build path", "Go executable build path",
                project, new FileChooserDescriptor(false, true, false, false, false, false));

        workingDirectoryBrowser.addBrowseFolderListener("Application working directory", "Application working directory",
                project, new FileChooserDescriptor(false, true, false, false, false, false));

        buildBeforeRunCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                buildDirectoryPathBrowser.setEnabled(buildBeforeRunCheckBox.isSelected());
            }
        });

        m_gdbPath.addBrowseFolderListener("GDB executable path", "GDB executable path",
                project, new FileChooserDescriptor(true, false, false, false, false, false));
    }

    private void setChosenFile(VirtualFile virtualFile) {
        applicationName.setText(virtualFile.getPath());
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
