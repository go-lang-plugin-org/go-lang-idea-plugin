package ro.redeul.google.go.ide;

import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SearchableConfigurable;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.GoIcons;

import javax.swing.*;

/**
 * Author: Florin Patan <florinpatan@gmail.com>
 */
public class GoGlobalConfigurable implements SearchableConfigurable  {

    private GoGlobalConfigurableForm configurableForm;

    @NotNull
    @Override
    public String getId() {
        return getHelpTopic();
    }

    @Override
    public Runnable enableSearch(String option) {
        return null;
    }

    @Nls
    @Override
    public String getDisplayName() {
        return "Google Go";
    }

    public Icon getIcon() {
        return GoIcons.GO_ICON_16x16;
    }

    @NotNull
    @Override
    public String getHelpTopic() {
        return "reference.settingsdialog.go";
    }

    @Override
    public JComponent createComponent() {
        configurableForm = new GoGlobalConfigurableForm();
        return configurableForm.componentPanel;
    }

    @Override
    public boolean isModified() {
        return configurableForm.isModified();
    }

    @Override
    public void apply() throws ConfigurationException {
        configurableForm.apply();
    }

    @Override
    public void reset() {
        configurableForm.reset();
    }

    @Override
    public void disposeUIResources() {
        configurableForm = null;
    }
}
