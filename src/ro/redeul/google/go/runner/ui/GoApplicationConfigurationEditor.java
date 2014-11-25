package ro.redeul.google.go.runner.ui;

import com.intellij.ide.util.TreeFileChooser;
import com.intellij.ide.util.TreeFileChooserFactory;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
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
    private JLabel gdbVersionWarning;
    private TextFieldWithBrowseButton m_gdbPath;
    private JCheckBox autoStartGdb;
    private JTextArea m_startupCommands;
    private RawCommandLineEditor m_runBuilderArguments;
    private RawCommandLineEditor m_runExecutableName;
    private JRadioButton goFileRadioButton;
    private JRadioButton goPackageRadioButton;
    private TextFieldWithBrowseButton applicationPackage;
    private JCheckBox enableDebuggingCheckBox;
    private JPanel runPanel;
    private JPanel debugPanel;

    @Override
    protected void resetEditorFrom(GoApplicationConfiguration configuration) {
        applicationName.setText(configuration.scriptName);
        applicationPackage.setText(configuration.packageDir);
        appArguments.setText(configuration.scriptArguments);
        m_runBuilderArguments.setText(configuration.runBuilderArguments);
        m_runExecutableName.setText(configuration.runExecutableName);
        goFileRadioButton.setSelected(!configuration.runPackage);
        goPackageRadioButton.setSelected(configuration.runPackage);
        applicationName.setEnabled(!configuration.runPackage);
        applicationPackage.setEnabled(configuration.runPackage);
        buildBeforeRunCheckBox.setSelected(configuration.goBuildBeforeRun);
        enableDebuggingCheckBox.setSelected(configuration.runDebugger);
        buildDirectoryPathBrowser.setEnabled(configuration.goBuildBeforeRun);
        buildDirectoryPathBrowser.setText(configuration.goOutputDir);
        workingDirectoryBrowser.setText(configuration.workingDir);
        if (workingDirectoryBrowser.getText().isEmpty()) {
            workingDirectoryBrowser.setText(configuration.getProject().getBasePath());
        }

        envVars.setText(configuration.envVars);

        //Debug stuff
        debugPanel.setEnabled(configuration.runDebugger);
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
        if (applicationName.getText().length() == 0 && !goPackageRadioButton.isSelected())
            throw new ConfigurationException("Please select the go file to run.");
        if (applicationPackage.getText().length() == 0 && goPackageRadioButton.isSelected())
            throw new ConfigurationException("Please select the package directory to build.");
        if (buildBeforeRunCheckBox.isSelected() && buildDirectoryPathBrowser.getText().equals("")) {
            throw new ConfigurationException("Please select the directory for the executable.");
        }
        if (!buildBeforeRunCheckBox.isSelected() && goPackageRadioButton.isSelected()) {
            throw new ConfigurationException("A package has to be built before it can be run. Please check the box build-before-run or select a go file to run instead of a package.");
        }

        configuration.scriptName = applicationName.getText();
        configuration.packageDir = applicationPackage.getText();
        configuration.runPackage = goPackageRadioButton.isSelected();
        configuration.scriptArguments = appArguments.getText();
        configuration.runBuilderArguments = m_runBuilderArguments.getText();
        configuration.runExecutableName = m_runExecutableName.getText();
        configuration.goBuildBeforeRun = buildBeforeRunCheckBox.isSelected();
        configuration.runDebugger = enableDebuggingCheckBox.isSelected();
        configuration.goOutputDir = buildDirectoryPathBrowser.getText();
        configuration.workingDir = workingDirectoryBrowser.getText();
        configuration.envVars = envVars.getText();

        //Debug stuff
        String gdbPath = m_gdbPath.getText();
        if (enableDebuggingCheckBox.isSelected()) {
            if (gdbPath.isEmpty()) {
                throw new ConfigurationException("Please select the path to gdb.");
            } else if (!GoGdbUtil.doesExecutableExist(gdbPath) || !GoGdbUtil.isValidGdbPath(gdbPath)) {
                throw new ConfigurationException("Please select a valid path to gdb.");
            } else {
                gdbVersionWarning.setVisible(!GoGdbUtil.isKnownGdb(gdbPath));
            }
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
                                                if (file instanceof GoFile) {
                                                    return ((GoFile) file).getMainFunction() != null;
                                                }

                                                return false;
                                            }
                                        }, true, false);

                        fileChooser.showDialog();

                        PsiFile selectedFile = fileChooser.getSelectedFile();
                        if (selectedFile != null) {
                            applicationName.setText(selectedFile.getVirtualFile().getPath());
                        }
                    }
                });

        applicationPackage.addBrowseFolderListener("Application package directory", "Application package directory",
                project, new FileChooserDescriptor(false, true, false, false, false, false));

        buildDirectoryPathBrowser.addBrowseFolderListener("Go executable build path", "Go executable build path",
                project, new FileChooserDescriptor(false, true, false, false, false, false));

        workingDirectoryBrowser.addBrowseFolderListener("Application working directory", "Application working directory",
                project, new FileChooserDescriptor(false, true, false, false, false, false));

        buildBeforeRunCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                buildDirectoryPathBrowser.setEnabled(buildBeforeRunCheckBox.isSelected());
                enableDebuggingCheckBox.setEnabled(buildBeforeRunCheckBox.isSelected());
            }
        });

        goFileRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                applicationName.setEnabled(goFileRadioButton.isSelected());
                applicationPackage.setEnabled(!goFileRadioButton.isSelected());
            }
        });

        goPackageRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                applicationName.setEnabled(!goPackageRadioButton.isSelected());
                applicationPackage.setEnabled(goPackageRadioButton.isSelected());
            }
        });

        m_gdbPath.addBrowseFolderListener("GDB executable path", "GDB executable path",
                project, new FileChooserDescriptor(true, false, false, false, false, false));

        debugPanel.setEnabled(enableDebuggingCheckBox.isSelected());
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
