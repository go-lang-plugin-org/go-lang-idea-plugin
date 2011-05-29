package ro.redeul.google.go.ide;

import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.GoIcons;

import javax.swing.*;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: 5/29/11
 * Time: 11:19 AM
 */
public class GoConfigurable implements SearchableConfigurable {

    GoConfigurableForm goConfigurableForm;

    Project project;

    public GoConfigurable(Project project) {
        this.project = project;
    }

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
        return "Go Settings";
    }

    @Override
    public Icon getIcon() {
        return GoIcons.GO_ICON_16x16;
    }

    @Override
    public String getHelpTopic() {
        return "reference.settingsdialog.project.go";
    }

    @Override
    public JComponent createComponent() {
        goConfigurableForm = new GoConfigurableForm();
        return goConfigurableForm.componentPanel;
    }

    @Override
    public boolean isModified() {
        return goConfigurableForm != null && goConfigurableForm.isModified(getProjectSettings().getState());
    }

    @Override
    public void apply() throws ConfigurationException {
        GoProjectSettings.GoProjectSettingsBean bean = new GoProjectSettings.GoProjectSettingsBean();

        if ( goConfigurableForm != null ) {
            goConfigurableForm.apply(bean);
            getProjectSettings().loadState(bean);
        }
    }

    private GoProjectSettings getProjectSettings() {
        return GoProjectSettings.getInstance(project);
    }

    @Override
    public void reset() {
        if ( goConfigurableForm != null ) {
            goConfigurableForm.reset(getProjectSettings().getState());
        }
    }

    @Override
    public void disposeUIResources() {
        goConfigurableForm.componentPanel = null;
        goConfigurableForm = null;
    }
}
