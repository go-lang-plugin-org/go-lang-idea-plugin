package ro.redeul.google.go.wizards.importWizard;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.ModifiableModuleModel;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ContentEntry;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.ui.configuration.ModulesProvider;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.packaging.artifacts.ModifiableArtifactModel;
import com.intellij.projectImport.ProjectImportBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.GoIcons;

import javax.swing.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by sinz on 01.03.14.
 */
public class GoProjectImportBuilder extends ProjectImportBuilder<String> {
    private static final Logger LOG = Logger.getInstance(GoProjectImportBuilder.class);
    @NotNull
    @Override
    public String getName() {
        return "Go";
    }

    @Override
    public Icon getIcon() {
        return GoIcons.GO_ICON_13x13;
    }

    @Override
    public List<String> getList() {
        return new ArrayList<String>();
    }

    @Override
    public boolean isMarked(String element) {
        return false;
    }

    @Override
    public void setList(List<String> list) throws ConfigurationException {
    }

    @Override
    public void setOpenProjectSettingsAfter(boolean on) {

    }

    @Nullable
    @Override
    public List<Module> commit(final Project project, ModifiableModuleModel model, ModulesProvider modulesProvider, ModifiableArtifactModel artifactModel) {
        //Create Module and iml-file
        final ModifiableModuleModel moduleModel = model != null ? model : ModuleManager.getInstance(project).getModifiableModel();
        Module myModule = moduleModel.newModule(project.getBasePath() + File.separator + project.getName() + ".iml", "GO_MODULE");
        final ModifiableRootModel mrm = ModuleRootManager.getInstance(myModule).getModifiableModel();
        mrm.inheritSdk();

        ApplicationManager.getApplication().runWriteAction(new Runnable() {
            @Override
            public void run() {
                //Add the default content entry
                VirtualFile baseDir = project.getBaseDir();
                ContentEntry entry = mrm.addContentEntry(baseDir);

                //Set src-folder as SourceFolder, if it exists
                VirtualFile srcFolder = baseDir.findFileByRelativePath("src");
                if(srcFolder != null) {
                    entry.addSourceFolder(srcFolder, false);
                }

                mrm.commit();
                moduleModel.commit();
            }
        });


        return Arrays.asList(myModule);
    }
}
