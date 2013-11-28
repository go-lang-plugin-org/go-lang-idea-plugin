package ro.redeul.google.go.ide;

import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.module.ModuleTypeManager;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ui.configuration.ModulesProvider;
import ro.redeul.google.go.GoBundle;
import ro.redeul.google.go.GoIcons;
import ro.redeul.google.go.config.sdk.GoAppEngineSdkType;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

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

    @Override
    public GoAppEngineModuleBuilder createModuleBuilder() {
        return new GoAppEngineModuleBuilder();
    }

    @Override
    public String getName() {
        return GoBundle.message("go.app.engine.module.type.name");
    }

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

    public boolean isValidSdk(final Module module, final Sdk projectSdk) {
        return projectSdk.getSdkType() == GoAppEngineSdkType.getInstance();
    }

    @Override
    public ModuleWizardStep[] createWizardSteps(WizardContext wizardContext, GoAppEngineModuleBuilder moduleBuilder, ModulesProvider modulesProvider)
    {
        List<ModuleWizardStep> steps = new ArrayList<ModuleWizardStep>();

//        ProjectWizardStepFactory factory = ProjectWizardStepFactory.getInstance();
//        steps.add(factory.createSourcePathsStep(wizardContext, moduleBuilder, null, "reference.dialogs.new.project.fromScratch.source"));
//        steps.add(factory.createProjectJdkStep(wizardContext));
//        steps.add(new AndroidModuleWizardStep(moduleBuilder, wizardContext.getProject()));
//        steps.add(factory.createSourcePathsStep(wizardContext, moduleBuilder, null, "reference.dialogs.new.project.fromScratch.source"));
//        steps.add(factory.createProjectJdkStep(wizardContext, SdkType.findInstance(GoAppEngineSdkType.class), moduleBuilder, new Computable.PredefinedValueComputable<Boolean>(true), null, ""));
//        steps.add(new GoModuleWizardStep(moduleBuilder, wizardContext.getProject()));
        return steps.toArray(new ModuleWizardStep[steps.size()]);
    }
}
