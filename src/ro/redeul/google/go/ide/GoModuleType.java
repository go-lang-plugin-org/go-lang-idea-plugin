package ro.redeul.google.go.ide;

import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ide.util.projectWizard.ProjectWizardStepFactory;
import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.module.ModuleTypeManager;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.SdkType;
import com.intellij.openapi.roots.ui.configuration.ModulesProvider;
import com.intellij.openapi.util.Computable;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.GoBundle;
import ro.redeul.google.go.GoIcons;
import ro.redeul.google.go.config.sdk.GoSdkType;
import ro.redeul.google.go.ide.ui.GoModuleWizardProjectSettings;
import ro.redeul.google.go.ide.ui.GoModuleWizardStepPackageConfig;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: Mihai Claudiu Toader <mtoader@gmail.com>
 * <p/>
 * Date: 1/2/11
 * Time: 10:34 AM
 */
public class GoModuleType extends ModuleType<GoModuleBuilder> {

    public static final String MODULE_TYPE_ID = "GO_MODULE";

    public GoModuleType() {
        super(MODULE_TYPE_ID);
    }

    public static GoModuleType getInstance() {
        return (GoModuleType) ModuleTypeManager.getInstance().findByID(MODULE_TYPE_ID);
    }

    @NotNull
    @Override
    public GoModuleBuilder createModuleBuilder() {
        return new GoModuleBuilder();
    }

    @NotNull
    @Override
    public String getName() {
        return GoBundle.message("go.module.type.name");
    }

    @NotNull
    @Override
    public String getDescription() {
        return GoBundle.message("go.module.type.description");
    }

    @Override
    public Icon getBigIcon() {
        return GoIcons.GO_ICON_24x24;
    }

    @Override
    public Icon getNodeIcon(boolean isOpened) {
        return GoIcons.GO_ICON_16x16;
    }

    public boolean isValidSdk(@NotNull final Module module, final Sdk projectSdk) {
        return projectSdk.getSdkType() == GoSdkType.getInstance();
    }

    @NotNull
    @Override
    public ModuleWizardStep[] createWizardSteps(@NotNull WizardContext wizardContext, @NotNull GoModuleBuilder moduleBuilder, @NotNull ModulesProvider modulesProvider)
    {
        List<ModuleWizardStep> steps = new ArrayList<ModuleWizardStep>();

        ProjectWizardStepFactory factory = ProjectWizardStepFactory.getInstance();

        steps.add(new GoModuleWizardStepPackageConfig(moduleBuilder));
        steps.add(new GoModuleWizardProjectSettings(moduleBuilder));
        steps.add(factory.createProjectJdkStep(wizardContext, SdkType.findInstance(GoSdkType.class), moduleBuilder, new Computable.PredefinedValueComputable<Boolean>(true), null, ""));
        return steps.toArray(new ModuleWizardStep[steps.size()]);
    }
}
