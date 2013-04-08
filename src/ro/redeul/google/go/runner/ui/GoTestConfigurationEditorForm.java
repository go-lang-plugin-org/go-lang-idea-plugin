package ro.redeul.google.go.runner.ui;

import java.util.Collection;
import java.util.Vector;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.intellij.execution.ui.ConfigurationModuleSelector;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.stubs.GoNamesCache;
import ro.redeul.google.go.runner.GoTestConfiguration;

public class GoTestConfigurationEditorForm
    extends SettingsEditor<GoTestConfiguration> {

    private JCheckBox filter;
    private JCheckBox useShort;
    private JCheckBox runTestBeforeBenchmark;
    private JPanel panel;
    private JComboBox packages;
    private JTextField testsFilter;
    private JRadioButton benchmark;
    private JRadioButton test;
    private JComboBox modules;
    private ButtonGroup testsGroup;

    private ConfigurationModuleSelector moduleSelector;
    private Project project;

    public GoTestConfigurationEditorForm(final Project project) {
        this.project = project;

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
    }

    @Override
    protected void resetEditorFrom(GoTestConfiguration s) {
        switch (s.executeWhat) {
            case Test:
                test.setSelected(true);
                break;
            case Benchmark:
                benchmark.setSelected(true);
                break;
        }
        runTestBeforeBenchmark.setSelected(s.testBeforeBenchmark);
        if (s.filter == null || s.filter.isEmpty()) {
            updateTestsFilterField();
        } else {
            filter.setSelected(true);
            testsFilter.setText(s.filter);
            updateTestsFilterField();
        }

        packages.getModel().setSelectedItem(s.packageName);
        useShort.setSelected(s.useShortRun);
        moduleSelector.reset(s);
    }

    private void updateTestsFilterField() {
        testsFilter.setEnabled(filter.isSelected());
        testsFilter.setEditable(filter.isSelected());
    }

    @Override
    protected void applyEditorTo(GoTestConfiguration s)
        throws ConfigurationException {
        Object selectedItem = packages.getSelectedItem();
        s.packageName = selectedItem != null ? selectedItem.toString() : "";
        s.filter = filter.isSelected() ? testsFilter.getText() : "";
        s.executeWhat = test.isSelected() ? GoTestConfiguration.Type.Test : GoTestConfiguration.Type.Benchmark;
        s.useShortRun = this.useShort.isSelected();
        s.testBeforeBenchmark = runTestBeforeBenchmark.isSelected();
        moduleSelector.applyTo(s);
    }

    @NotNull
    @Override
    protected JComponent createEditor() {
        moduleSelector = new ConfigurationModuleSelector(project, modules);
        return panel;
    }

    @Override
    protected void disposeEditor() {
        panel.setVisible(false);
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}
