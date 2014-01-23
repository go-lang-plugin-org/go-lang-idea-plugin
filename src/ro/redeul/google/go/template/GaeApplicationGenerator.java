package ro.redeul.google.go.template;

import com.intellij.ide.util.projectWizard.WebProjectTemplate;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.ProjectJdkTable;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.impl.ProjectJdkImpl;
import com.intellij.openapi.projectRoots.impl.SdkConfigurationUtil;
import com.intellij.openapi.roots.*;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.startup.StartupManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiManager;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.GoBundle;
import ro.redeul.google.go.GoIcons;
import ro.redeul.google.go.actions.GoTemplatesFactory;
import ro.redeul.google.go.config.sdk.GoAppEngineSdkData;
import ro.redeul.google.go.config.sdk.GoAppEngineSdkType;
import ro.redeul.google.go.sdk.GoSdkUtil;

import javax.swing.*;
import java.util.Arrays;

public class GaeApplicationGenerator extends WebProjectTemplate {
    private static final Logger LOG = Logger.getInstance(GaeApplicationGenerator.class);

    @NotNull
    @Override
    public String getName() {
        return GoBundle.message("go.app.engine.application.name");
    }

    @Nullable
    @Override
    public String getDescription() {
        return GoBundle.message("go.app.engine.application.description");
    }

    @Override
    public Icon getIcon() {
        return GoIcons.GAE_ICON_16x16;
    }

    @Override
    public void generateProject(@NotNull final Project project,
                                @NotNull final VirtualFile baseDir,
                                @NotNull Object settings,
                                @NotNull final Module module) {

        if (!(settings instanceof GoAppEngineSdkData)) {
            return;
        }

        if (baseDir.getCanonicalPath() == null) {
            return;
        }

        final String goRootPath = ((GoAppEngineSdkData) settings).SDK_HOME_PATH;

        StartupManager.getInstance(project).runWhenProjectIsInitialized(new Runnable() {
            @Override
            public void run() {ApplicationManager.getApplication().runWriteAction(new Runnable() {
                @Override
                public void run() {
                    ModifiableRootModel model = ModuleRootManager.getInstance(module).getModifiableModel();

                    PsiDirectory directory = PsiManager.getInstance(project).findDirectory(GoSdkUtil.getVirtualFile(baseDir.getCanonicalPath()));

                    if (directory == null) {
                        return;
                    }

                    try {
                        directory.checkCreateFile("app.yaml");
                        GoTemplatesFactory.createFromTemplate(directory, "yaml", "app.yaml", GoTemplatesFactory.Template.GoAppEngineConfig);
                    } catch (IncorrectOperationException ignored) {
                    } catch (Exception e) {
                        LOG.error(e.getMessage());
                    }

                    try {
                        directory.checkCreateFile(module.getProject().getName().concat(".go"));
                        GoTemplatesFactory.createFromTemplate(directory, "main", project.getName().concat(".go"), GoTemplatesFactory.Template.GoAppEngineMain);
                    } catch (IncorrectOperationException ignored) {
                    } catch (Exception e) {
                        LOG.error(e.getMessage());
                    }

                    VirtualFileManager.getInstance().syncRefresh();

                    GoAppEngineSdkData sdkData = GoSdkUtil.testGoAppEngineSdk(goRootPath);

                    if (sdkData == null) {
                        // skip since the folder isn't a proper go sdk
                        return;
                    }

                    GoAppEngineSdkType gaeSdkType = GoAppEngineSdkType.getInstance();
                    if (gaeSdkType.getSdkData() == null) {
                        gaeSdkType.setSdkData(sdkData);
                    }

                    Sdk existingSdk = ProjectJdkTable.getInstance().findJdk(gaeSdkType.getSdkLongName());

                    if (existingSdk == null) {
                        ProjectJdkTable jdkTable = ProjectJdkTable.getInstance();

                        String newSdkName = SdkConfigurationUtil.createUniqueSdkName(gaeSdkType, sdkData.SDK_HOME_PATH, Arrays.asList(jdkTable.getAllJdks()));
                        ProjectJdkImpl goSdk = new ProjectJdkImpl(newSdkName, gaeSdkType);

                        goSdk.setHomePath(goRootPath);

                        gaeSdkType.setupSdkPaths(goSdk);
                        jdkTable.addJdk(goSdk);

                        ProjectRootManager.getInstance(project).setProjectSdk(goSdk);
                    } else {
                        ProjectRootManager.getInstance(project).setProjectSdk(existingSdk);
                    }
                }
            });}
        });
    }

    @NotNull
    @Override
    public GeneratorPeer createPeer() {
        return new GaeGeneratorPeer();
    }

    private void updateModules(@NotNull Project project, @NotNull Library lib, boolean remove) {
        Module[] modules = ModuleManager.getInstance(project).getModules();
        for (Module module : modules) {
            ModifiableRootModel model = ModuleRootManager.getInstance(module).getModifiableModel();
            if (!remove) {
                if (model.findLibraryOrderEntry(lib) == null) {
                    LibraryOrderEntry entry = model.addLibraryEntry(lib);
                    entry.setScope(DependencyScope.PROVIDED);
                }
            }
            else {
                LibraryOrderEntry entry = model.findLibraryOrderEntry(lib);
                if (entry != null) {
                    model.removeOrderEntry(entry);
                }
            }
            model.commit();
        }
    }
}
