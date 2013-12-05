package ro.redeul.google.go.options;

import com.intellij.codeInsight.CodeInsightSettings;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.components.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ui.configuration.libraries.LibraryPresentationManager;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.ArrayUtil;
import com.intellij.util.xmlb.XmlSerializerUtil;
import com.intellij.webcore.ScriptingFrameworkDescriptor;
import com.intellij.webcore.libraries.ScriptingLibraryModel;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.GoBundle;
import ro.redeul.google.go.sdk.GoSdkUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
