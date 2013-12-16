package ro.redeul.google.go.ide;

import com.intellij.ide.util.projectWizard.JavaModuleBuilder;
import com.intellij.ide.util.projectWizard.ModuleBuilderListener;
import com.intellij.ide.util.projectWizard.SourcePathsBuilder;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.projectRoots.SdkTypeId;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.newvfs.impl.VirtualDirectoryImpl;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiManager;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.actions.GoTemplatesFactory;
import ro.redeul.google.go.config.sdk.GoSdkType;

import java.io.IOException;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: 1/2/11
 * Time: 10:34 AM
 */
public class GoModuleBuilder extends JavaModuleBuilder implements SourcePathsBuilder, ModuleBuilderListener {

    public GoModuleBuilder() {
        addListener(this);
    }

    @Override
    public void moduleCreated(@NotNull Module module) {

        ModuleRootManager moduleRootManager = ModuleRootManager.getInstance(module);
        VirtualFile sourceRoots[] = moduleRootManager.getSourceRoots();

        if (sourceRoots.length != 1 ) {
            return;
        }

        PsiDirectory directory = PsiManager.getInstance(module.getProject()).findDirectory(sourceRoots[0]);

        if (directory == null || directory.getParentDirectory() == null) {
            return;
        }

        final PsiDirectory baseDir = directory.getParentDirectory();

        ApplicationManager.getApplication().runWriteAction(new Runnable() {
            @Override
            public void run() {
                baseDir.createSubdirectory("bin");
                baseDir.createSubdirectory("pkg");
            }
        });

        GoTemplatesFactory.createFromTemplate(directory, "main", module.getProject().getName().concat(".go"), GoTemplatesFactory.Template.GoAppMain);
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
