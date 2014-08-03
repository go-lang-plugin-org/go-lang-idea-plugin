package ro.redeul.google.go.ide.ui;

import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import ro.redeul.google.go.ide.GoPackageModuleBuilder;

import javax.swing.*;

/**
 * Created by holgerfinger on 03.08.14.
 */
public class GoPackageModuleWizardStep extends ModuleWizardStep {
    private JTextField txtPackageURL;
    private JPanel panelGoPackageSettings;
    private GoPackageModuleBuilder moduleBuilder;

    public GoPackageModuleWizardStep(GoPackageModuleBuilder moduleBuilder) {
        this.moduleBuilder = moduleBuilder;
    }

    public void updateStep() {

    }

    public void onStepLeaving() {
        this.moduleBuilder.setPackageURL(this.txtPackageURL.getText());
    }

    public boolean validate() {
        return !this.txtPackageURL.getText().isEmpty();
    }

    @Override
    public JComponent getComponent() {
        return panelGoPackageSettings;
    }

    @Override
    public void updateDataModel() {
        this.moduleBuilder.setPackageURL(this.txtPackageURL.getText());
    }
}
