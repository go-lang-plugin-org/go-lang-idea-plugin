package ro.redeul.google.go.ide;

import com.intellij.ide.util.projectWizard.JavaModuleBuilder;
import com.intellij.ide.util.projectWizard.ModuleBuilderListener;
import com.intellij.ide.util.projectWizard.SourcePathsBuilder;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.projectRoots.SdkTypeId;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiManager;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.actions.GoTemplatesFactory;
import ro.redeul.google.go.config.sdk.GoSdkType;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: 1/2/11
 * Time: 10:34 AM
 */
public class GoModuleBuilder extends JavaModuleBuilder implements SourcePathsBuilder, ModuleBuilderListener {

    private static final Logger LOG = Logger.getInstance(GoModuleBuilder.class);

    public GoModuleBuilder() {
        addListener(this);
    }

    @Override
    public void moduleCreated(@NotNull final Module module) {

        ModuleRootManager moduleRootManager = ModuleRootManager.getInstance(module);
        final VirtualFile sourceRoots[] = moduleRootManager.getSourceRoots();
        final String projectName = module.getProject().getName();


        if (sourceRoots.length != 1 ) {
            return;
        }

        final PsiDirectory directory = PsiManager.getInstance(module.getProject()).findDirectory(sourceRoots[0]);

        if (directory == null || directory.getParentDirectory() == null) {
            return;
        }

        final PsiDirectory baseDir = directory.getParentDirectory();

        ApplicationManager.getApplication().runWriteAction(new Runnable() {
            @Override
            public void run() {
                try {
                    baseDir.createSubdirectory("bin");
                    baseDir.createSubdirectory("pkg");

                    //Create package folder under src and add main.go
                    PsiDirectory mainPackage = directory.createSubdirectory("main");
                    mainPackage.checkCreateFile(projectName.concat(".go"));
                    GoTemplatesFactory.createFromTemplate(mainPackage, "main", projectName.concat(".go"), GoTemplatesFactory.Template.GoAppMain);
                } catch (IncorrectOperationException ignored) {
                } catch (Exception e) {
                    LOG.error(e.getMessage());
                }
            }
        });

    }

    @Override
    public ModuleType getModuleType() {
        return GoModuleType.getInstance();
    }

    @Override
    public boolean isSuitableSdkType(SdkTypeId sdkType) {
        return sdkType instanceof GoSdkType;
    }

}
