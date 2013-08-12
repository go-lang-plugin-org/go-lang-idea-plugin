package ro.redeul.google.go.ide;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.components.StorageScheme;
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

    public enum BuildSystemType {
        Internal, Makefile, Install
    }

    public static class GoProjectSettingsBean {
        public BuildSystemType BUILD_SYSTEM_TYPE = BuildSystemType.Internal;
        public boolean enableVariablesCompletion = true;
        public boolean enableOptimizeImports = false;
    }

    GoProjectSettingsBean bean;

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
