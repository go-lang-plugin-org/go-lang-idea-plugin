package ro.redeul.google.go.template;

import com.intellij.ide.util.projectWizard.WebProjectTemplate;
import com.intellij.openapi.application.ApplicationManager;
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
import java.io.IOException;
import java.util.Arrays;

public class GoApplicationGenerator extends WebProjectTemplate {
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

        ApplicationManager.getApplication().runWriteAction(new Runnable() {
            @Override
            public void run() {


                try {
                    baseDir.createChildDirectory(this, "bin");
                    baseDir.createChildDirectory(this, "pkg");
                    sourceDir[0] = baseDir.createChildDirectory(this, "src");
                } catch (Exception ignored) {

                }
            }
        });


        StartupManager.getInstance(project).runWhenProjectIsInitialized(new Runnable() {
            @Override
            public void run() {ApplicationManager.getApplication().runWriteAction(new Runnable() {
                @Override
                public void run() {
                        ModifiableRootModel model = ModuleRootManager.getInstance(module).getModifiableModel();

                        if (sourceDir[0] != null) {
                            model.addContentEntry(sourceDir[0].getParent()).addSourceFolder(sourceDir[0], false);
                            model.commit();
                        }

                        GoSdkData sdkData = GoSdkUtil.testGoogleGoSdk(goRootPath);

                        if ( sdkData == null ) {
                            // skip since the folder isn't a proper go sdk
                            return;
                        }

                        final ProjectJdkTable jdkTable = ProjectJdkTable.getInstance();
                        ProjectJdkImpl goSdk = null;
                        final GoSdkType goSdkType = GoSdkType.getInstance();

                        if (goSdkType.getSdkData() == null) {
                            goSdkType.setSdkData(sdkData);
                        }

                        Sdk existingSdk = ProjectJdkTable.getInstance().findJdk(goSdkType.getSdkLongName());

                        if (existingSdk == null) {
                            try {
                                String newSdkName = SdkConfigurationUtil.createUniqueSdkName(goSdkType, sdkData.GO_GOROOT_PATH, Arrays.asList(jdkTable.getAllJdks()));
                                goSdk = new ProjectJdkImpl(newSdkName, goSdkType);

                                goSdk.setHomePath(goRootPath);

                                final ProjectJdkImpl finalGoSdk = goSdk;
                                ApplicationManager.getApplication().runWriteAction(new Runnable() {
                                    @Override
                                    public void run() {
                                        goSdkType.setupSdkPaths(finalGoSdk);
                                        jdkTable.addJdk(finalGoSdk);
                                    }
                                });

                            } catch (Exception ignored) {

                            }
                        } else {
                            goSdk = (ProjectJdkImpl) existingSdk;
                        }

                        ProjectRootManager.getInstance(project).setProjectSdk(goSdk);

                        PsiDirectory directory = PsiManager.getInstance(project).findDirectory(GoSdkUtil.getVirtualFile(baseDir.getCanonicalPath().concat("/src")));

                        if ( directory == null ) {
                            return;
                        }

                        try {
                            GoTemplatesFactory.createFromTemplate(directory, "main", project.getName().concat(".go"), GoTemplatesFactory.Template.GoAppMain);
                        } catch(IncorrectOperationException ignored) {

                        }

                        VirtualFileManager.getInstance().syncRefresh();
                    }
                });
            }
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
