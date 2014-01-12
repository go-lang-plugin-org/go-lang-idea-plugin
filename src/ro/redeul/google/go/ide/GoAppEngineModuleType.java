package ro.redeul.google.go.ide;

import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.module.ModuleTypeManager;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ui.configuration.ModulesProvider;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.GoBundle;
import ro.redeul.google.go.GoIcons;
import ro.redeul.google.go.config.sdk.GoAppEngineSdkType;

import javax.swing.*;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: 1/2/11
 * Time: 10:34 AM
 */
public class GoAppEngineModuleType extends ModuleType<GoAppEngineModuleBuilder> {

    private static final String MODULE_TYPE_ID = "GO_APP_ENGINE_MODULE";

    public GoAppEngineModuleType() {
        super(MODULE_TYPE_ID);
    }

    public static GoAppEngineModuleType getInstance() {
        return (GoAppEngineModuleType) ModuleTypeManager.getInstance().findByID(MODULE_TYPE_ID);
    }

    @NotNull
    @Override
    public GoAppEngineModuleBuilder createModuleBuilder() {
        return new GoAppEngineModuleBuilder();
    }

    @NotNull
    @Override
    public String getName() {
        return GoBundle.message("go.app.engine.module.type.name");
    }

    @NotNull
    @Override
    public String getDescription() {
        return GoBundle.message("go.app.engine.module.type.description");
    }

    @Override
    public Icon getBigIcon() {
        return GoIcons.GAE_ICON_24x24;
    }

    @Override
    public Icon getNodeIcon(boolean isOpened) {
        return GoIcons.GAE_ICON_16x16;
    }

    public boolean isValidSdk(@NotNull final Module module, final Sdk projectSdk) {
        return projectSdk.getSdkType() == GoAppEngineSdkType.getInstance();
    }

    @NotNull
    @Override
    public ModuleWizardStep[] createWizardSteps(@NotNull WizardContext wizardContext, @NotNull GoAppEngineModuleBuilder moduleBuilder, @NotNull ModulesProvider modulesProvider)
    {
        return new ModuleWizardStep[]{};
    }
}
