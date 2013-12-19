package ro.redeul.google.go.options;

import com.intellij.codeInsight.CodeInsightSettings;
import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.components.*;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.GoBundle;

import java.io.File;

@State(
    name="GoogleGoSettings",
    storages= {
        @Storage(
            file = "$APP_CONFIG$/settings.golang.xml"
        )}
)
public class GoSettings implements PersistentStateComponent<GoSettings>, ExportableComponent {
    public boolean SHOW_IMPORT_POPUP = true;
    public boolean OPTIMIZE_IMPORTS_ON_THE_FLY = true;
    public String goRoot = "";
    public String goPath = "";

    public GoSettings() {
        CodeInsightSettings codeInsightSettings = CodeInsightSettings.getInstance();
        OPTIMIZE_IMPORTS_ON_THE_FLY = codeInsightSettings.OPTIMIZE_IMPORTS_ON_THE_FLY;
    }

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
