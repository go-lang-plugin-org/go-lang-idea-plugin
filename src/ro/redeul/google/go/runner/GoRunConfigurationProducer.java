package ro.redeul.google.go.runner;

import com.intellij.execution.Location;
import com.intellij.execution.RunManagerEx;
import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.execution.actions.ConfigurationContext;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.junit.RuntimeConfigurationProducer;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.GoFile;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: Aug 19, 2010
 * Time: 2:49:31 PM
 */
public class GoRunConfigurationProducer extends RuntimeConfigurationProducer {

    PsiElement element;

    public GoRunConfigurationProducer() {
        super(GoRunConfigurationType.getInstance());
    }

    protected GoRunConfigurationProducer(ConfigurationFactory configurationFactory) {
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

        if ( goFile.getMainFunction() == null ) {
            return null;
        }

        return createConfiguration(goFile, context.getModule());
    }

    private GoFile locationToFile(Location location) {
        final PsiElement element = location.getPsiElement();
        final PsiFile file = element.getContainingFile();

        if (!(file instanceof GoFile)) {
            return null;
        }

        return (GoFile) file;
    }

    private RunnerAndConfigurationSettings createConfiguration(GoFile goFile, Module module) {
        final Project project = goFile.getProject();

        element = goFile;

        RunnerAndConfigurationSettings settings = RunManagerEx.getInstanceEx(project).createConfiguration("", getConfigurationFactory());
        GoApplicationConfiguration applicationConfiguration = (GoApplicationConfiguration) settings.getConfiguration();

        final PsiDirectory dir = goFile.getContainingDirectory();
        if (dir == null)
            return null;

        applicationConfiguration.setName(goFile.getName());
        VirtualFile scriptFile = goFile.getOriginalFile().getVirtualFile();
        if ( scriptFile == null ) {
            return null;
        }

        applicationConfiguration.scriptName = scriptFile.getCanonicalPath();
        applicationConfiguration.setModule(module);

        return settings;
    }

    @Override
    public int compareTo(Object o) {
        return -1;
    }

    protected RunnerAndConfigurationSettings findExistingByElement(Location location,
                                                                   @NotNull RunnerAndConfigurationSettings[] existingConfigurations,
                                                                   ConfigurationContext context) {
        for (RunnerAndConfigurationSettings existingConfiguration : existingConfigurations) {
            final RunConfiguration configuration = existingConfiguration.getConfiguration();

            if ( ! (configuration instanceof GoApplicationConfiguration) ) {
                continue;
            }

            GoApplicationConfiguration goApplicationConfiguration = (GoApplicationConfiguration) configuration;

            GoFile goFile = locationToFile(location);
            if ( goFile != null ) {
                VirtualFile virtualFile = goFile.getVirtualFile();
                if ( virtualFile != null && virtualFile.getPath().endsWith(FileUtil.toSystemIndependentName(goApplicationConfiguration.scriptName))) {
                    return existingConfiguration;
                }
            }
        }

        return null;
    }
}
