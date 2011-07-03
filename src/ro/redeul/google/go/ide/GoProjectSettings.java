package ro.redeul.google.go.ide;

import com.intellij.openapi.components.*;
import com.intellij.openapi.project.Project;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: 5/29/11
 * Time: 11:41 AM
 */
@State(
        name = "GoProjectSettings",
        storages = {
                @Storage(id = "default", file = "$PROJECT_FILE$"),
                @Storage(id = "dir", file = "$PROJECT_CONFIG_DIR$/go_settings.xml", scheme = StorageScheme.DIRECTORY_BASED)
        }
)
public class GoProjectSettings implements PersistentStateComponent<GoProjectSettings.GoProjectSettingsBean> {

    public enum BuildSystemType {
        Internal, Makefile
    }

    public static class GoProjectSettingsBean {
        public BuildSystemType BUILD_SYSTEM_TYPE = BuildSystemType.Internal;
        public boolean enableVariablesCompletion = true;
    }

    GoProjectSettingsBean bean;

    @Override
    public GoProjectSettingsBean getState() {
        return bean != null ? bean : new GoProjectSettingsBean();
    }

    @Override
    public void loadState(GoProjectSettingsBean settingsBean) {
        this.bean = settingsBean;
    }

    public static GoProjectSettings getInstance(Project project) {
        return ServiceManager.getService(project, GoProjectSettings.class);
    }

}
