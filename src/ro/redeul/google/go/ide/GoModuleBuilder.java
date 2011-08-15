package ro.redeul.google.go.ide;

import com.intellij.ide.util.projectWizard.JavaModuleBuilder;
import com.intellij.ide.util.projectWizard.ModuleBuilderListener;
import com.intellij.ide.util.projectWizard.SourcePathsBuilder;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiManager;
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

    @Override
    public void moduleCreated(@NotNull Module module) {

        ModuleRootManager moduleRootManager = ModuleRootManager.getInstance(module);
        VirtualFile sourceRoots[] = moduleRootManager.getSourceRoots();

        if (sourceRoots.length > 1 ) {
            return;
        }

        PsiDirectory directory = PsiManager.getInstance(module.getProject()).findDirectory(sourceRoots[0]);

        if ( directory != null ) {
            GoTemplatesFactory.createFromTemplate(directory, "main", "main.go", GoTemplatesFactory.Template.GoAppMain);
        }
    }

    @Override
    public void setupRootModel(ModifiableRootModel modifiableRootModel) throws ConfigurationException {
        addListener(this);
        super.setupRootModel(modifiableRootModel);

        modifiableRootModel.inheritSdk();
        modifiableRootModel.commit();

        ProjectRootManager projectRootManager = ProjectRootManager.getInstance(modifiableRootModel.getProject());
        projectRootManager.setProjectSdk(getModuleJdk());
    }

    @Override
    public ModuleType getModuleType() {
        return GoModuleType.getInstance();
    }

    @Override
    public boolean isSuitableSdk(Sdk sdk) {
        return sdk.getSdkType() == GoSdkType.getInstance();
    }
}
