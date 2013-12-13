package ro.redeul.google.go.ide;

import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SearchableConfigurable;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.GoIcons;

import javax.swing.*;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: 8/15/11
 * Time: 9:57 AM
 */
public class GoAppEngineSettingsConfigurable implements SearchableConfigurable  {

    private GoAppEngineProjectSettingsConfigurableForm configurableForm;

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
        return "Google Go App Engine";
    }

    public Icon getIcon() {
        return GoIcons.GO_ICON_16x16;
    }

    @Override
    public String getHelpTopic() {
        return "reference.settingsdialog.go.app.engine";
    }

    @Override
    public JComponent createComponent() {
        configurableForm = new GoAppEngineProjectSettingsConfigurableForm();
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
