package ro.redeul.google.go.options;

import java.io.File;

import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.components.ExportableComponent;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.GoBundle;

@State(
    name="GoogleGoSettings",
    storages= {
        @Storage(
            file = "$APP_CONFIG$/editor.codeinsight.xml"
        )}
)
public class GoSettings implements PersistentStateComponent<GoSettings>, ExportableComponent {
    public boolean SHOW_IMPORT_POPUP = true;
    public boolean OPTIMIZE_IMPORTS_ON_THE_FLY = false;

    public static GoSettings getInstance() {
        return ServiceManager.getService(GoSettings.class);
    }

    @NotNull
    @Override
    public File[] getExportFiles() {
        return new File[]{PathManager.getOptionsFile("editor.codeinsight")};
    }

    @NotNull
    @Override
    public String getPresentableName() {
        return GoBundle.message("go.settings");
    }

    @Override
    public GoSettings getState() {
        return this;
    }

    @Override
    public void loadState(GoSettings state) {
        XmlSerializerUtil.copyBean(state, this);
    }
}
