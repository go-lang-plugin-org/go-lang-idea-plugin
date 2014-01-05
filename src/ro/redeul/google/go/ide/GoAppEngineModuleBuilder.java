package ro.redeul.google.go.ide;

import com.intellij.ide.util.projectWizard.JavaModuleBuilder;
import com.intellij.ide.util.projectWizard.ModuleBuilderListener;
import com.intellij.ide.util.projectWizard.SourcePathsBuilder;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.projectRoots.SdkTypeId;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiManager;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.actions.GoTemplatesFactory;
import ro.redeul.google.go.config.sdk.GoAppEngineSdkType;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: 1/2/11
 * Time: 10:34 AM
 */
public class GoAppEngineModuleBuilder extends JavaModuleBuilder implements SourcePathsBuilder, ModuleBuilderListener {

    private static final Logger LOG = Logger.getInstance(GoAppEngineModuleBuilder.class);

    public GoAppEngineModuleBuilder() {
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

        try {
            GoTemplatesFactory.createFromTemplate(directory, "main", "main.go", GoTemplatesFactory.Template.GoAppEngineMain);
            GoTemplatesFactory.createFromTemplate(directory.getParent(), "yaml", "app.yaml", GoTemplatesFactory.Template.GoAppEngineConfig);
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }

    }

    @Override
    public ModuleType getModuleType() {
        return GoAppEngineModuleType.getInstance();
    }

    @Override
    public boolean isSuitableSdkType(SdkTypeId sdkType) {
        return sdkType instanceof GoAppEngineSdkType;
    }

}
