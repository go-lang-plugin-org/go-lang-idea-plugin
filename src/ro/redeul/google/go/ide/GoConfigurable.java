package ro.redeul.google.go.ide;

import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.options.GoSettings;

import javax.swing.*;

import static ro.redeul.google.go.GoIcons.GO_ICON_16x16;

public class GoConfigurable implements SearchableConfigurable {

    private GoConfigurableForm form;

    private final Project project;

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
        return "Google Go";
    }

    public Icon getIcon() {
        return GO_ICON_16x16;
    }

    @Override
    @NotNull
    public String getHelpTopic() {
        return "reference.settingsdialog.project.go";
    }

    @Override
    public JComponent createComponent() {
        form = new GoConfigurableForm();
        form.enableShowHide();
        return form.componentPanel;
    }

    @Override
    public boolean isModified() {
        return form != null &&
            form.isModified(getProjectSettings().getState(),
                            GoSettings.getInstance().getState());
    }

    @Override
    public void apply() throws ConfigurationException {
        GoProjectSettings.GoProjectSettingsBean projectSettings = new GoProjectSettings.GoProjectSettingsBean();
        GoSettings settings = GoSettings.getInstance().getState();

        if ( form != null ) {
            form.apply(projectSettings, settings);
            GoSettings.getInstance().loadState(settings);
            getProjectSettings().loadState(projectSettings);
        }
    }

    private GoProjectSettings getProjectSettings() {
        return GoProjectSettings.getInstance(project);
    }

    @Override
    public void reset() {
        if ( form != null ) {
            form.reset(getProjectSettings().getState(), GoSettings.getInstance().getState());
        }
    }

    @Override
    public void disposeUIResources() {
        form = null;
    }
}
