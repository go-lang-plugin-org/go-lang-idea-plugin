package ro.redeul.google.go.runner;

import com.intellij.execution.Location;
import com.intellij.execution.actions.ConfigurationContext;
import com.intellij.execution.actions.RunConfigurationProducer;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.patterns.ElementPattern;
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
import static com.intellij.patterns.StandardPatterns.string;

public class GoTestConfigurationProducer extends RunConfigurationProducer {

    private static final Logger LOG = Logger.getInstance(GoTestConfigurationProducer.class);

    public GoTestConfigurationProducer() {
        super(GoTestConfigurationType.getInstance());
    }

    protected GoTestConfigurationProducer(ConfigurationFactory configurationFactory) {
        super(configurationFactory);
    }

    @Override
    public boolean isConfigurationFromContext(RunConfiguration configuration, ConfigurationContext context) {
        if (configuration.getType() != getConfigurationType())
            return false;

        GoFile file = locationToFile(context.getLocation());
        if (file == null || !file.getName().endsWith("_test.go")) {
            return false;
        }

        PsiElement target = locationToTestFunction(context.getPsiLocation());
        GoTestConfiguration testConfig = (GoTestConfiguration) configuration;

        try {
            VirtualFile virtualFile = file.getVirtualFile();
            if (virtualFile == null)
                return false;

            Project project = file.getProject();
            Module module = ProjectRootManager.getInstance(project).getFileIndex().getModuleForFile(virtualFile);
            GoApplicationModuleBasedConfiguration configurationModule = testConfig.getConfigurationModule();
            String packageName = file.getPackageName();

            if (!StringUtil.equals(testConfig.packageDir, file.getContainingDirectory().getVirtualFile().getCanonicalPath()) ||
                    !StringUtil.equals(testConfig.workingDir, project.getBasePath()) ||
                    !(testConfig.testTargetType == GoTestConfiguration.TestTargetType.Package) ||
                    !(StringUtil.equals(packageName, file.getPackageName())) ||
                    !(configurationModule != null && module != null && module.equals(configurationModule.getModule())))
                return false;

            if (target instanceof GoFile) {
                GoTestConfiguration.Type executionMode =
                        fileDirContainsTestsOfSamePackage(project, (GoFile) target)
                                ? GoTestConfiguration.Type.Test
                                : GoTestConfiguration.Type.Benchmark;

                return testConfig.executeWhat == executionMode &&
                        StringUtil.isEmptyOrSpaces(testConfig.filter);
            } else if (target instanceof GoFunctionDeclaration) {

                GoFunctionDeclaration function = (GoFunctionDeclaration) target;

                if (!StringUtil.equals(testConfig.filter, "^" + function.getFunctionName() + "$"))
                    return false;

                if (FUNCTION_TEST.accepts(target))
                    return testConfig.executeWhat == GoTestConfiguration.Type.Test;

                if (FUNCTION_BENCHMARK.accepts(target))
                    return testConfig.executeWhat == GoTestConfiguration.Type.Benchmark;
            }

            return false;
        } catch (Exception ex) {
            LOG.error(ex);
        }

        return false;
    }

    @Override
    protected boolean setupConfigurationFromContext(RunConfiguration configuration, ConfigurationContext context, Ref sourceElement) {
        if (context.getPsiLocation() == null) {
            return false;
        }

        PsiFile file = context.getPsiLocation().getContainingFile();
        if (!(file instanceof GoFile)) {
            return false;
        }

        if (!file.getName().endsWith("_test.go")) {
            return false;
        }

        GoFile goFile = (GoFile) file;
        PsiElement psiSourceElement = (PsiElement) sourceElement.get();

        try {
            VirtualFile virtualFile = file.getVirtualFile();
            if (virtualFile == null) {
                return false;
            }

            psiSourceElement = locationToTestFunction(psiSourceElement);

            Project project = file.getProject();
            Module module = ProjectRootManager.getInstance(project).getFileIndex().getModuleForFile(virtualFile);
            GoTestConfiguration testConfig = (GoTestConfiguration) configuration;

            String packageName = goFile.getFullPackageName();
            testConfig.packageName = packageName;
            testConfig.packageDir = goFile.getContainingDirectory().getVirtualFile().getCanonicalPath();

            testConfig.testTargetType = GoTestConfiguration.TestTargetType.Package;

            if (psiSourceElement instanceof GoFile) {
                configuration.setName(packageName);
                // If there is any tests in current package, run in test mode.
                // Otherwise run in benchmark mode.
                if (fileDirContainsTestsOfSamePackage(project, (GoFile) psiSourceElement)) {
                    testConfig.executeWhat = GoTestConfiguration.Type.Test;
                } else {
                    testConfig.executeWhat = GoTestConfiguration.Type.Benchmark;
                }
            } else if (FUNCTION_TEST.accepts(psiSourceElement)) {
                String name = ((GoFunctionDeclaration) psiSourceElement).getName();
                configuration.setName(packageName + "." + name);

                testConfig.executeWhat = GoTestConfiguration.Type.Test;
                testConfig.filter = "^" + name + "$";
            } else if (FUNCTION_BENCHMARK.accepts(psiSourceElement)) {
                String name = ((GoFunctionDeclaration) psiSourceElement).getName();
                configuration.setName(packageName + "." + name);

                testConfig.executeWhat = GoTestConfiguration.Type.Benchmark;
                testConfig.filter = "^" + name + "$";
            }

            testConfig.workingDir = project.getBasePath();
            testConfig.setModule(module);

            return true;
        } catch (Exception ex) {
            LOG.error(ex);
        }

        return false;
    }

    private static final ElementPattern<GoFunctionDeclaration> FUNCTION_BENCHMARK =
            psiElement(GoFunctionDeclaration.class)
                    .withParent(psiElement(GoFile.class))
                    .withChild(
                            psiElement(GoFunctionParameterList.class)
                                    .withChild(
                                            psiElement(GoFunctionParameter.class)
                                                    .withChild(
                                                            psiElement(GoPsiTypePointer.class).withText("*testing.B")
                                                    )
                                    )
                                    .afterSibling(
                                            psiElement(GoLiteralIdentifier.class)
                                                    .withText(string().matches("Benchmark.*"))));

    private static final ElementPattern<GoFunctionDeclaration> FUNCTION_TEST =
            psiElement(GoFunctionDeclaration.class)
                    .withParent(psiElement(GoFile.class))
                    .withChild(
                            psiElement(GoFunctionParameterList.class)
                                    .withChild(
                                            psiElement(GoFunctionParameter.class)
                                                    .withChild(
                                                            psiElement(GoPsiTypePointer.class)
                                                                    .withText("*testing.T")
                                                    )
                                    )
                                    .afterSibling(
                                            psiElement(GoLiteralIdentifier.class)
                                                    .withText(string().matches("Test.*"))));

    private PsiElement locationToTestFunction(PsiElement location) {
        if (location == null)
            return null;

        while (location != null &&
                !FUNCTION_BENCHMARK.accepts(location) &&
                !FUNCTION_TEST.accepts(location) &&
                !(location instanceof GoFile)) {
            location = location.getParent();
        }

        return location;
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

    private static String getPackageNameForTesting(GoFile file) {
        String pkgName = file.getPackageImportPath();
        if (pkgName.endsWith("_test")) {
            String fileName = file.getName();
            String testedFileName = fileName.replace("_test", "");
            try {
                GoFile testedFile = (GoFile) file.getParent().findFile(testedFileName);

                return testedFile.getPackageImportPath();
            } catch (NullPointerException ignored) {

            }

        }
        return pkgName;
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
        if (relativePath == null) {
            return "";
        }

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
}
