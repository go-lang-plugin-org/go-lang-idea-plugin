package ro.redeul.google.go.ide;

import com.intellij.ide.util.projectWizard.JavaModuleBuilder;
import com.intellij.ide.util.projectWizard.ModuleBuilderListener;
import com.intellij.ide.util.projectWizard.SourcePathsBuilder;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.projectRoots.SdkTypeId;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiManager;
import com.intellij.util.IncorrectOperationException;
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

        PsiDirectory directory = PsiManager.getInstance(module.getProject()).findDirectory(module.getProject().getBaseDir());

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
            directory.checkCreateFile("main.go");
            GoTemplatesFactory.createFromTemplate(directory, "main", "main.go", GoTemplatesFactory.Template.GoAppEngineMain);
        } catch (IncorrectOperationException ignored) {
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
