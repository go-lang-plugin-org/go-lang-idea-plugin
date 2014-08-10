package ro.redeul.google.go.ide;

import com.intellij.ide.util.projectWizard.ModuleWizardStep;

import javax.swing.*;

/**
 * Created by holgerfinger on 10.08.14.
 */
public class GoPackageModuleWizardSettings extends ModuleWizardStep {
    private GoPackageModuleBuilder moduleBuilder;
    private GoConfigurableForm form;
    private GoProjectSettings.GoProjectSettingsBean settingsBean;

    public GoPackageModuleWizardSettings(GoPackageModuleBuilder moduleBuilder) {
        this.moduleBuilder = moduleBuilder;
        form = new GoConfigurableForm();
    }

    @Override
    public JComponent getComponent() {
        return form.componentPanel;
    }

    @Override
    public void updateDataModel() {
        form.apply(this.moduleBuilder.settings);
    }
}
