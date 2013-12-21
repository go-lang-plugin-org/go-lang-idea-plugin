package ro.redeul.google.go.wizards;

import com.intellij.ide.util.newProjectWizard.ProjectNameStep;
import com.intellij.ide.util.newProjectWizard.StepSequence;
import com.intellij.ide.util.newProjectWizard.modes.WizardMode;
import com.intellij.ide.util.projectWizard.ProjectBuilder;
import com.intellij.ide.util.projectWizard.ProjectWizardStepFactory;
import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ui.configuration.ModulesProvider;
import com.intellij.openapi.util.Computable;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.config.sdk.GoSdkType;
import ro.redeul.google.go.ide.GoModuleBuilder;
import ro.redeul.google.go.sdk.GoSdkUtil;

import java.util.List;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: 8/15/11
 * Time: 7:54 AM
 */
public class GoApplicationWizard extends WizardMode {

    private final GoModuleBuilder goModuleBuilder;

    public GoApplicationWizard() {
        goModuleBuilder = new GoModuleBuilder();
    }

    @NotNull
    @Override
    public String getDisplayName(WizardContext context) {
        return "Go application from scratch";
    }

    @NotNull
    @Override
    public String getDescription(WizardContext context) {
        return "Will create a new sample Go application";
    }

    @Override
    public boolean isAvailable(WizardContext context) {
        return context.isCreatingNewProject();
    }

    @Override
    protected StepSequence createSteps(@NotNull WizardContext context, @NotNull ModulesProvider modulesProvider) {
        StepSequence sequence = new StepSequence();

        ProjectWizardStepFactory factory = ProjectWizardStepFactory.getInstance();

        final boolean isNewProject = context.getProject() == null;
        if (isNewProject) {
            sequence.addCommonStep(new ProjectNameStep(context, this));
        }

        sequence.addCommonStep(factory.createProjectJdkStep(context, GoSdkType.getInstance(), goModuleBuilder, new Computable<Boolean>() {
            @Override
            public Boolean compute() {
                List<Sdk> sdkList = GoSdkUtil.getSdkOfType(GoSdkType.getInstance());
                return !(sdkList != null && sdkList.size() == 1);

            }
        }, null, ""));

        return sequence;
    }

    @Override
    public ProjectBuilder getModuleBuilder() {
        return goModuleBuilder;
    }

    @Override
    public void onChosen(boolean enabled) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
