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
import ro.redeul.google.go.GoBundle;
import ro.redeul.google.go.GoIcons;
import ro.redeul.google.go.config.sdk.GoSdkType;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: mtoader
 * Date: 1/2/11
 * Time: 10:34 AM
 * To change this template use File | Settings | File Templates.
 */
public class GoModuleType extends ModuleType<GoModuleBuilder> {

    public static final String MODULE_TYPE_ID = "GO_MODULE";

    public GoModuleType() {
        super(MODULE_TYPE_ID);
    }

    public static GoModuleType getInstance() {
        return (GoModuleType) ModuleTypeManager.getInstance().findByID(MODULE_TYPE_ID);
    }

    @Override
    public GoModuleBuilder createModuleBuilder() {
        return new GoModuleBuilder();
    }

    @Override
    public String getName() {
        return GoBundle.message("go.module.type.name");
    }

    @Override
    public String getDescription() {
        return GoBundle.message("go.module.type.description");
    }

    @Override
    public Icon getBigIcon() {
        return GoIcons.GO_ICON_16x16;
    }

    @Override
    public Icon getNodeIcon(boolean isOpened) {
        return GoIcons.GO_ICON_16x16;
    }

    public boolean isValidSdk(final Module module, final Sdk projectSdk) {
        return true;
    }

    @Override
    public ModuleWizardStep[] createWizardSteps(WizardContext wizardContext, GoModuleBuilder moduleBuilder, ModulesProvider modulesProvider) {

        List<ModuleWizardStep> steps = new ArrayList<ModuleWizardStep>();

        ProjectWizardStepFactory factory = ProjectWizardStepFactory.getInstance();

//        steps.add(factory.createSourcePathsStep(wizardContext, moduleBuilder, null, "reference.dialogs.new.project.fromScratch.source"));
//        steps.add(factory.createProjectJdkStep(wizardContext));
//        steps.add(new AndroidModuleWizardStep(moduleBuilder, wizardContext.getProject()));
        steps.add(factory.createSourcePathsStep(wizardContext, moduleBuilder, null, "reference.dialogs.new.project.fromScratch.source"));
        steps.add(factory.createProjectJdkStep(wizardContext, SdkType.findInstance(GoSdkType.class), moduleBuilder, new Computable.PredefinedValueComputable<Boolean>(true), null, ""));
//        steps.add(new GoModuleWizardStep(moduleBuilder, wizardContext.getProject()));
        return steps.toArray(new ModuleWizardStep[steps.size()]);
    }
}
