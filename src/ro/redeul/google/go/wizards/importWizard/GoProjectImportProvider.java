package ro.redeul.google.go.wizards.importWizard;

import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ide.util.projectWizard.ProjectWizardStepFactory;
import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.openapi.projectRoots.SdkType;
import com.intellij.openapi.util.Computable;
import com.intellij.projectImport.ProjectImportBuilder;
import com.intellij.projectImport.ProjectImportProvider;
import ro.redeul.google.go.config.sdk.GoSdkType;
import ro.redeul.google.go.ide.GoModuleBuilder;

/**
 * Created by sinz on 01.03.14.
 */
public class GoProjectImportProvider extends ProjectImportProvider {
    protected GoProjectImportProvider(final GoProjectImportBuilder builder) {
        super(builder);
    }

    @Override
    public ModuleWizardStep[] createSteps(WizardContext context) {
        ProjectWizardStepFactory factory = ProjectWizardStepFactory.getInstance();

        //Add a step into the importer, with which the user can choose the used Go SDK
        return new ModuleWizardStep[] {factory.createProjectJdkStep(context,
			SdkType.findInstance(GoSdkType.class), new GoModuleBuilder(),
			new Computable.PredefinedValueComputable<Boolean>(true), null, "")};
    }
}
