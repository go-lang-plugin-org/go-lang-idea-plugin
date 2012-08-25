package ro.redeul.google.go.runner;

import com.intellij.execution.Location;
import com.intellij.execution.RunManagerEx;
import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.execution.actions.ConfigurationContext;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.junit.RuntimeConfigurationProducer;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.patterns.ElementPattern;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionParameter;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionParameterList;
import ro.redeul.google.go.lang.psi.types.GoPsiTypePointer;
import static com.intellij.patterns.PlatformPatterns.psiElement;
import static com.intellij.patterns.StandardPatterns.collection;
import static com.intellij.patterns.StandardPatterns.string;

public class GoTestConfigurationProducer extends RuntimeConfigurationProducer {

    PsiElement element;

    public GoTestConfigurationProducer() {
        super(GoTestConfigurationType.getInstance());
    }

    protected GoTestConfigurationProducer(ConfigurationFactory configurationFactory) {
        super(configurationFactory);
    }

    @Override
    public PsiElement getSourceElement() {
        return element;
    }

    @Override
    protected RunnerAndConfigurationSettings createConfigurationByElement(Location location, ConfigurationContext context) {
        GoFile goFile = locationToFile(location);

        if (goFile == null) return null;

        VirtualFile virtualFile = goFile.getVirtualFile();
        if (virtualFile == null)
            return null;

        if (!virtualFile.getNameWithoutExtension().endsWith("_test"))
            return null;

        return createConfiguration(goFile, context.getModule(),
                                   location.getPsiElement());
    }

    private static ElementPattern<GoFunctionDeclaration> FUNCTION_BENCHMARK =
        psiElement(GoFunctionDeclaration.class)
            .withParent(psiElement(GoFile.class))
            .withChild(
                psiElement(GoFunctionParameterList.class)
                    .withChild(
                        psiElement(GoFunctionParameter.class)
                            .withChildren(
                                collection(PsiElement.class)
                                    .first(
                                        psiElement(GoLiteralIdentifier.class))
                                    .last(psiElement(GoPsiTypePointer.class)
                                              .withText("*testing.B"))))
                    .afterSibling(
                        psiElement(GoLiteralIdentifier.class)
                            .withText(string().matches("Benchmark.*"))));

    private static ElementPattern<GoFunctionDeclaration> FUNCTION_TEST =
        psiElement(GoFunctionDeclaration.class)
            .withParent(psiElement(GoFile.class))
            .withChild(
                psiElement(GoFunctionParameterList.class)
                    .withChild(
                        psiElement(GoFunctionParameter.class)
                            .withChildren(
                                collection(PsiElement.class)
                                    .first(
                                        psiElement(GoLiteralIdentifier.class))
                                    .last(psiElement(GoPsiTypePointer.class)
                                              .withText("*testing.T"))))
                    .afterSibling(
                        psiElement(GoLiteralIdentifier.class)
                            .withText(string().matches("Test.*"))));

    private RunnerAndConfigurationSettings createConfiguration(GoFile goFile, Module module, PsiElement element) {

        final Project project = goFile.getProject();

        RunnerAndConfigurationSettings settings =
            RunManagerEx.getInstanceEx(project)
                        .createConfiguration("", getConfigurationFactory());

        GoTestConfiguration testConfiguration =
            (GoTestConfiguration) settings.getConfiguration();

        final PsiDirectory dir = goFile.getContainingDirectory();
        if (dir == null)
            return null;

        while (!(element instanceof GoFile) &&
            !FUNCTION_BENCHMARK.accepts(element) &&
            !FUNCTION_TEST.accepts(element) ) {
            element = element.getParent();
        }

        this.element = element;

        if (element instanceof GoFile) {
            testConfiguration.setName(goFile.getPackageImportPath());
            testConfiguration.executeWhat = GoTestConfiguration.Type.Test;
        } else if (FUNCTION_TEST.accepts(element)) {
            String name = ((GoFunctionDeclaration) element).getName();
            testConfiguration.setName(name);
            testConfiguration.executeWhat = GoTestConfiguration.Type.Test;
            testConfiguration.filter = "^" + name +"$";
        } else if (FUNCTION_BENCHMARK.accepts(element)) {
            String name = ((GoFunctionDeclaration) element).getName();
            testConfiguration.setName(name);
            testConfiguration.executeWhat = GoTestConfiguration.Type.Benchmark;
            testConfiguration.filter = "^" + name +"$";
        }

        testConfiguration.packageName = goFile.getPackageImportPath();
        testConfiguration.useShortRun = false;

        return settings;
    }

    private GoFile locationToFile(Location location) {
        final PsiElement element = location.getPsiElement();
        final PsiFile file = element.getContainingFile();

        if (!(file instanceof GoFile)) {
            return null;
        }

        return (GoFile) file;
    }


    @Override
    public int compareTo(Object o) {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
