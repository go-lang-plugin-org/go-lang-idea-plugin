package ro.redeul.google.go.ide.ui;

import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.openapi.options.ConfigurationException;
import ro.redeul.google.go.ide.GoConfigurableForm;
import ro.redeul.google.go.ide.GoModuleBuilder;
import ro.redeul.google.go.ide.GoProjectSettings;

import javax.swing.*;

public class GoModuleWizardProjectSettings extends ModuleWizardStep {
    private GoModuleBuilder moduleBuilder;
    private GoConfigurableForm form;
    private GoProjectSettings.GoProjectSettingsBean settingsBean;

    public GoModuleWizardProjectSettings(GoModuleBuilder moduleBuilder) {
        this.moduleBuilder = moduleBuilder;
        form = new GoConfigurableForm();
    }

    @Override
    public JComponent getComponent() {
        return form.componentPanel;
    }

    @Override
    public void updateDataModel() {
        try {
            form.apply(this.moduleBuilder.settings);
        } catch (ConfigurationException ignored) {

        }
    }
}
