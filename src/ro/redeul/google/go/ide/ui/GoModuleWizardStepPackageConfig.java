package ro.redeul.google.go.ide.ui;

import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import ro.redeul.google.go.ide.GoConfigurableForm;
import ro.redeul.google.go.ide.GoModuleBuilder;
import ro.redeul.google.go.ide.GoProjectSettings;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by holgerfinger on 03.08.14.
 */
public class GoModuleWizardStepPackageConfig extends ModuleWizardStep {
    private JTextField txtPackageURL;
    private JPanel panelGoPackageSettings;
    private JRadioButton radioNewPackage;
    private JRadioButton radioDownloadPackage;
    private JTextField txtNewPackageName;
    private JCheckBox enablePrependSysGoPath;
    private JCheckBox enableAppendSysGoPath;
    private JButton buttonProjectSettings;
    private JPanel panelTest;
    private GoModuleBuilder moduleBuilder;
    private GoConfigurableForm form;
    private GoProjectSettings.GoProjectSettingsBean settingsBean;

    public GoModuleWizardStepPackageConfig(GoModuleBuilder moduleBuilder) {
        this.moduleBuilder = moduleBuilder;



        radioNewPackage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                updateCompponents();
            }
        });
        radioDownloadPackage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                updateCompponents();
            }
        });
    }

    private void updateCompponents() {
        txtPackageURL.setEnabled(radioDownloadPackage.isSelected());
        txtNewPackageName.setEnabled(radioNewPackage.isSelected());
    }

    @Override
    public void updateStep() {

    }

    @Override
    public void onStepLeaving() {

    }


    @Override
    public boolean validate() {
        if (radioDownloadPackage.isSelected()) {
            return !this.txtPackageURL.getText().isEmpty();
        }
        else {
            return !this.txtNewPackageName.getText().isEmpty();
        }
    }

    @Override
    public JComponent getComponent() {
        return panelGoPackageSettings;
    }

    @Override
    public void updateDataModel() {
        this.moduleBuilder.packageURL = this.txtPackageURL.getText();
        this.moduleBuilder.isNew = this.radioNewPackage.isSelected();
        this.moduleBuilder.packageName = this.txtNewPackageName.getText();
    }

    private void createUIComponents() {
        form = new GoConfigurableForm();
        form.enableShowHide();
        panelTest = new JPanel();
        panelTest.add(form.componentPanel);

    }
}
