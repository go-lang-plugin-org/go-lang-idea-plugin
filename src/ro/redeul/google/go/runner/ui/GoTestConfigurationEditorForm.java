package ro.redeul.google.go.runner.ui;

import com.intellij.ide.util.TreeFileChooser;
import com.intellij.ide.util.TreeFileChooserFactory;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.psi.PsiFile;
import com.intellij.ui.RawCommandLineEditor;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.GoFileType;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.stubs.GoNamesCache;
import ro.redeul.google.go.runner.GoTestConfiguration;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.Vector;

import static ro.redeul.google.go.runner.GoTestConfiguration.TestTargetType;
import static ro.redeul.google.go.runner.GoTestConfiguration.Type;

public class GoTestConfigurationEditorForm extends SettingsEditor<GoTestConfiguration> {

    private JCheckBox filter;
    private JCheckBox useShort;
    private JCheckBox runTestBeforeBenchmark;
    private JPanel panel;
    private JComboBox packages;
    private JTextField testsFilter;
    private JRadioButton benchmark;
    private JRadioButton test;
    private JRadioButton packageNameRadioButton;
    private JRadioButton testFileNameRadioButton;
    private RawCommandLineEditor testRunnerArguments;
    private TextFieldWithBrowseButton testFile;
    private RawCommandLineEditor testArguments;
    private RawCommandLineEditor envVars;
    private TextFieldWithBrowseButton workingDirectoryBrowser;
    private JCheckBox runGoVetBeforeCheckBox;
    private JRadioButton allTestsInCWDRadioButton;
    private ButtonGroup testsGroup;

    @SuppressWarnings("unchecked")
    public GoTestConfigurationEditorForm(final Project project) {

        filter.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                updateTestsFilterField();
            }
        });

        Vector<String> myPackages = new Vector<String>();
        Collection<String> allPackages =
            GoNamesCache.getInstance(project).getProjectPackages();

        for (String packageName : allPackages) {
            myPackages.add(packageName);
        }

        packages.setModel(new DefaultComboBoxModel(myPackages));
        packageNameRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                packages.setEnabled(packageNameRadioButton.isSelected());
                testFile.setEnabled(!packageNameRadioButton.isSelected());
            }
        });

        testFileNameRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                packages.setEnabled(packageNameRadioButton.isSelected());
                testFile.setEnabled(!packageNameRadioButton.isSelected());
            }
        });
        testFile.addActionListener(new ActionListener() {
            @Override
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

                                        return file.getName().contains("_test.go");
                                    }
                                }, true, false);

                fileChooser.showDialog();

                PsiFile selectedFile = fileChooser.getSelectedFile();
                if (selectedFile != null) {
                    testFile.setText(selectedFile.getVirtualFile().getPath());
                }
            }
        });
    }

    @Override
    protected void resetEditorFrom(GoTestConfiguration testConfiguration) {
        envVars.setText(testConfiguration.envVars);
        testRunnerArguments.setText(testConfiguration.testRunnerArgs);

        if (testConfiguration.testTargetType == TestTargetType.File) {
            allTestsInCWDRadioButton.setSelected(false);
            packageNameRadioButton.setSelected(false);
            testFileNameRadioButton.setSelected(true);
            packages.setEnabled(false);
            testFile.setEnabled(true);
        } else if (testConfiguration.testTargetType == TestTargetType.Package) {
            allTestsInCWDRadioButton.setSelected(false);
            packageNameRadioButton.setSelected(true);
            testFileNameRadioButton.setSelected(false);
            packages.setEnabled(true);
            testFile.setEnabled(false);
        } else {
            allTestsInCWDRadioButton.setSelected(true);
            packageNameRadioButton.setSelected(false);
            testFileNameRadioButton.setSelected(false);
            packages.setEnabled(false);
            testFile.setEnabled(false);
        }

        packages.getModel().setSelectedItem(testConfiguration.packageName);
        testFile.setText(testConfiguration.testFile);
        testArguments.setText(testConfiguration.testArgs);
        workingDirectoryBrowser.setText(testConfiguration.workingDir);

        if (workingDirectoryBrowser.getText().isEmpty()) {
            workingDirectoryBrowser.setText(testConfiguration.getProject().getBasePath());
        }

        if (testConfiguration.executeWhat == Type.Benchmark) {
            benchmark.setSelected(true);
        } else {
            test.setSelected(true);
        }

        if (testConfiguration.filter == null || testConfiguration.filter.isEmpty()) {
            updateTestsFilterField();
        } else {
            filter.setSelected(true);
            testsFilter.setText(testConfiguration.filter);
            updateTestsFilterField();
        }

        useShort.setSelected(testConfiguration.useShortRun);
        runTestBeforeBenchmark.setSelected(testConfiguration.testBeforeBenchmark);
        runGoVetBeforeCheckBox.setSelected(testConfiguration.goVetEnabled);
    }

    private void updateTestsFilterField() {
        testsFilter.setEnabled(filter.isSelected());
        testsFilter.setEditable(filter.isSelected());
    }

    @Override
    protected void applyEditorTo(GoTestConfiguration testConfiguration) throws ConfigurationException {
        Object selectedItem = packages.getSelectedItem();
        testConfiguration.envVars = envVars.getText();
        testConfiguration.testRunnerArgs = testRunnerArguments.getText();

        if (packageNameRadioButton.isSelected()) {
            testConfiguration.testTargetType =  TestTargetType.Package;
        } else if (testFileNameRadioButton.isSelected()) {
            testConfiguration.testTargetType = TestTargetType.File;
        } else if (allTestsInCWDRadioButton.isSelected()) {
            testConfiguration.testTargetType = TestTargetType.CWD;
        }


        testConfiguration.packageName = selectedItem != null ? selectedItem.toString() : "";
        testConfiguration.testFile = testFile.getText();
        testConfiguration.testArgs = testArguments.getText();
        testConfiguration.workingDir = workingDirectoryBrowser.getText();
        testConfiguration.executeWhat = test.isSelected() ? Type.Test : Type.Benchmark;
        testConfiguration.filter = filter.isSelected() ? testsFilter.getText() : "";
        testConfiguration.useShortRun = this.useShort.isSelected();
        testConfiguration.testBeforeBenchmark = runTestBeforeBenchmark.isSelected();
        testConfiguration.goVetEnabled = runGoVetBeforeCheckBox.isSelected();

        testConfiguration.checkConfiguration();
    }

    @NotNull
    @Override
    protected JComponent createEditor() {
        return panel;
    }

    @Override
    protected void disposeEditor() {
        panel.setVisible(false);
    }

}
