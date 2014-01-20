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
import ro.redeul.google.go.config.sdk.GoSdkData;
import ro.redeul.google.go.config.sdk.GoSdkType;
import ro.redeul.google.go.sdk.GoSdkUtil;

import javax.swing.*;
import java.util.Arrays;

public class GoApplicationGenerator extends WebProjectTemplate {
    private static final Logger LOG = Logger.getInstance(GoApplicationGenerator.class);

    @NotNull
    @Override
    public String getName() {
        return GoBundle.message("go.module.application.name");
    }

    @Nullable
    @Override
    public String getDescription() {
        return GoBundle.message("go.module.application.description");
    }

    @Override
    public Icon getIcon() {
        return GoIcons.GO_ICON_16x16;
    }

    @Override
    public void generateProject(@NotNull final Project project,
                                @NotNull final VirtualFile baseDir,
                                @NotNull Object settings,
                                @NotNull final Module module) {

        if (!(settings instanceof GoSdkData)) {
            return;
        }

        if (baseDir.getCanonicalPath() == null) {
            return;
        }

        final String goRootPath = ((GoSdkData) settings).GO_GOROOT_PATH;
        final VirtualFile[] sourceDir = {null};

        StartupManager.getInstance(project).runWhenProjectIsInitialized(new Runnable() {
            @Override
            public void run() {ApplicationManager.getApplication().runWriteAction(new Runnable() {
                @Override
                public void run() {
                    try {
                        baseDir.createChildDirectory(this, "bin");
                        baseDir.createChildDirectory(this, "pkg");
                        sourceDir[0] = baseDir.createChildDirectory(this, "src");
                    } catch (Exception e) {
                        LOG.error(e.getMessage());
                    }

                    ModifiableRootModel model = ModuleRootManager.getInstance(module).getModifiableModel();

                    if (sourceDir[0] != null) {
                        model.addContentEntry(sourceDir[0].getParent()).addSourceFolder(sourceDir[0], false);
                        model.commit();
                    }

                    PsiDirectory directory = PsiManager.getInstance(project).findDirectory(GoSdkUtil.getVirtualFile(baseDir.getCanonicalPath().concat("/src")));

                    if (directory == null) {
                        return;
                    }

                    try {
                        directory.checkCreateFile(module.getProject().getName().concat(".go"));
                        GoTemplatesFactory.createFromTemplate(directory, "main", project.getName().concat(".go"), GoTemplatesFactory.Template.GoAppMain);
                    } catch (IncorrectOperationException ignored) {
                    } catch (Exception e) {
                        LOG.error(e.getMessage());
                    }

                    VirtualFileManager.getInstance().syncRefresh();

                    GoSdkData sdkData = GoSdkUtil.testGoogleGoSdk(goRootPath);

                    if (sdkData == null) {
                        // skip since the folder isn't a proper go sdk
                        return;
                    }

                    GoSdkType goSdkType = GoSdkType.getInstance();
                    if (goSdkType.getSdkData() == null) {
                        goSdkType.setSdkData(sdkData);
                    }

                    Sdk existingSdk = ProjectJdkTable.getInstance().findJdk(goSdkType.getSdkLongName());

                    if (existingSdk == null) {
                        ProjectJdkTable jdkTable = ProjectJdkTable.getInstance();

                        String newSdkName = SdkConfigurationUtil.createUniqueSdkName(goSdkType, sdkData.GO_GOROOT_PATH, Arrays.asList(jdkTable.getAllJdks()));
                        ProjectJdkImpl goSdk = new ProjectJdkImpl(newSdkName, goSdkType);

                        goSdk.setHomePath(goRootPath);

                        goSdkType.setupSdkPaths(goSdk);
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
        return new GoGeneratorPeer();
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
