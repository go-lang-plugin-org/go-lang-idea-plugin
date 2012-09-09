package ro.redeul.google.go.runner;

import com.intellij.execution.Location;
import com.intellij.execution.RunManagerEx;
import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.execution.actions.ConfigurationContext;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.junit.RuntimeConfigurationProducer;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.patterns.ElementPattern;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import ro.redeul.google.go.GoFileType;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionParameter;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionParameterList;
import ro.redeul.google.go.lang.psi.toplevel.GoPackageDeclaration;
import ro.redeul.google.go.lang.psi.types.GoPsiTypePointer;
import ro.redeul.google.go.lang.psi.utils.GoFileUtils;

import java.io.File;

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

        String dottedPackagePath = goFile.getPackageImportPath().replace('/', '.');
        if (element instanceof GoFile) {
            testConfiguration.setName(dottedPackagePath);
            // If there is any tests in current package, run in test mode.
            // Otherwise run in benchmark mode.
            if (fileDirContainsTestsOfSamePackage(project, (GoFile) element)) {
                testConfiguration.executeWhat = GoTestConfiguration.Type.Test;
            } else {
                testConfiguration.executeWhat = GoTestConfiguration.Type.Benchmark;
            }
        } else if (FUNCTION_TEST.accepts(element)) {
            String name = ((GoFunctionDeclaration) element).getName();
            testConfiguration.setName(dottedPackagePath + "." + name);
            testConfiguration.executeWhat = GoTestConfiguration.Type.Test;
            testConfiguration.filter = "^" + name +"$";
        } else if (FUNCTION_BENCHMARK.accepts(element)) {
            String name = ((GoFunctionDeclaration) element).getName();
            testConfiguration.setName(dottedPackagePath + "." + name);
            testConfiguration.executeWhat = GoTestConfiguration.Type.Benchmark;
            testConfiguration.filter = "^" + name +"$";
        }

        testConfiguration.packageName = goFile.getPackageImportPath();
        testConfiguration.packageDir = getGoFileDirRelativePath(goFile);
        testConfiguration.setModule(module);
        testConfiguration.useShortRun = false;

        return settings;
    }

    private static boolean fileDirContainsTestsOfSamePackage(Project project, GoFile file) {
        VirtualFile virtualFile = file.getVirtualFile();
        if (virtualFile == null) {
            return false;
        }

        VirtualFile dir = virtualFile.getParent();
        if (dir == null || !dir.isDirectory()) {
            return false;
        }

        String packageName = getPackageName(file);
        if (packageName.isEmpty()) {
            return false;
        }

        for (VirtualFile child : dir.getChildren()) {
            if (child.getFileType() == GoFileType.INSTANCE &&
                    child.getNameWithoutExtension().endsWith("_test")) {
                GoFile childGoFile = (GoFile) PsiManager.getInstance(project).findFile(child);
                if (packageName.equals(getPackageName(childGoFile)) &&
                        fileContainsTest(childGoFile)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean fileContainsTest(GoFile file) {
        for (GoFunctionDeclaration func : GoFileUtils.getFunctionDeclarations(file)) {
            if (FUNCTION_TEST.accepts(func)) {
                return true;
            }
        }
        return false;
    }

    private static String getPackageName(GoFile file) {
        if (file == null) {
            return "";
        }

        GoPackageDeclaration pkg = file.getPackage();
        return pkg == null ? "" : pkg.getPackageName();
    }

    private String getGoFileDirRelativePath(GoFile goFile) {
        VirtualFile vf = goFile.getVirtualFile();
        if (vf == null) {
            return "";
        }

        vf = vf.getParent();
        String basePath = goFile.getProject().getBasePath();
        if (basePath == null || basePath.isEmpty()) {
            return "";
        }

        String filePath = vf.getPath();
        String relativePath = FileUtil.getRelativePath(basePath, filePath, File.separatorChar);
        return relativePath.replace(File.separatorChar, '/');
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
