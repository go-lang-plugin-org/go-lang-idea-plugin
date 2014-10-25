package ro.redeul.google.go.ide;

import com.intellij.openapi.components.*;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

@State(
        name = "GoProjectSettings",
        storages = {
                @Storage(id = "default", file = "$PROJECT_FILE$"),
                @Storage(id = "dir", file = "$PROJECT_CONFIG_DIR$/go_settings.xml", scheme = StorageScheme.DIRECTORY_BASED)
        }
)
public class GoProjectSettings implements PersistentStateComponent<GoProjectSettings.GoProjectSettingsBean> {

    public static class GoProjectSettingsBean {
        public boolean enableVariablesCompletion = true;
        public boolean enableOptimizeImports = false;
        public boolean prependGoPath = false;
        public boolean useGoPath = true;
        public boolean goFmtOnSave = true;
        public boolean goimportsOnSave = false;
        public String goimportsPath = "";
    }

    private GoProjectSettingsBean bean;

    @Override
    @NotNull
    public GoProjectSettingsBean getState() {
        return bean != null ? bean : new GoProjectSettingsBean();
    }

    @Override
    public void loadState(GoProjectSettingsBean settingsBean) {
        this.bean = settingsBean;
    }

    @NotNull
    public static GoProjectSettings getInstance(Project project) {
        return ServiceManager.getService(project, GoProjectSettings.class);
    }
}
