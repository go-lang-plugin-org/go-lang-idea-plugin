/*
* Copyright 2012 Midokura Europe SARL
*/
package ro.redeul.google.go.runner.ui;

import java.util.Collection;
import java.util.Vector;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.stubs.GoNamesCache;
import ro.redeul.google.go.runner.GoTestConfiguration;

public class GoTestConfigurationEditorForm
    extends SettingsEditor<GoTestConfiguration> {

    private JRadioButton allTestsRadioButton;
    private JRadioButton filterTestsRadioButton;
    private JCheckBox requireShortRunningTimeCheckBox;
    private JTextField testsRegexp;
    private JComboBox modules;
    private JPanel panel;
    private JComboBox packages;
    private ButtonGroup testsGroup;

    private Project project;

    public GoTestConfigurationEditorForm(final Project project) {
        this.project = project;

        filterTestsRadioButton.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                updateTestsFilterField();
            }
        });

        Vector<String> myPackages = new Vector<String>();
        Collection<String> allPackages = GoNamesCache.getInstance(project)
                                                     .getAllPackages();
        for (String packageName : allPackages) {
            myPackages.add(packageName);
        }
        packages.setModel(new DefaultComboBoxModel(myPackages));
    }

    @Override
    protected void resetEditorFrom(GoTestConfiguration s) {
        if (s.filteredTests == null || s.filteredTests.isEmpty()) {
            allTestsRadioButton.setSelected(true);
            updateTestsFilterField();
        } else {
            filterTestsRadioButton.setSelected(true);
            testsRegexp.setText(s.filteredTests);
            updateTestsFilterField();
        }
    }

    private void updateTestsFilterField() {
        testsRegexp.setEnabled(filterTestsRadioButton.isSelected());
        testsRegexp.setEditable(filterTestsRadioButton.isSelected());
    }

    @Override
    protected void applyEditorTo(GoTestConfiguration s)
        throws ConfigurationException {
        Object selectedItem = packages.getSelectedItem();
        s.packageName = selectedItem != null ? selectedItem.toString() : "";
        s.filteredTests =
            filterTestsRadioButton.isSelected()
                ? testsRegexp.getText() : "";

        s.useShortRun = this.requireShortRunningTimeCheckBox.isSelected();
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
