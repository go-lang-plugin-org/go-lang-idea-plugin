package ro.redeul.google.go.runner;

import com.intellij.execution.Location;
import com.intellij.execution.RunManagerEx;
import com.intellij.execution.RunnerAndConfigurationSettings;
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
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.GoFile;

import java.io.File;
import java.util.List;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: Aug 19, 2010
 * Time: 2:49:31 PM
 */
public class GoApplicationConfigurationProducer extends RunConfigurationProducer {

    private PsiElement element;

    private static final Logger LOG = Logger.getInstance(GoApplicationConfigurationProducer.class);

    public GoApplicationConfigurationProducer() {
        super(GoApplicationConfigurationType.getInstance());
    }

    protected GoApplicationConfigurationProducer(ConfigurationFactory configurationFactory) {
        super(configurationFactory);
    }

    @Override
    public boolean isConfigurationFromContext(RunConfiguration configuration, ConfigurationContext context) {
        if ( configuration.getType() != getConfigurationType() )
            return false;

        GoFile file = locationToFile(context.getLocation());
        if ( file == null || file.getMainFunction() == null)
            return false;

        VirtualFile virtualFile = file.getVirtualFile();
        if (virtualFile == null)
            return false;

        GoApplicationConfiguration goAppConfig = (GoApplicationConfiguration) configuration;

        try {

            Project project = file.getProject();
            Module module = ProjectRootManager.getInstance(project).getFileIndex().getModuleForFile(virtualFile);
            String name = file.getVirtualFile().getCanonicalPath();

            GoApplicationModuleBasedConfiguration configurationModule = goAppConfig.getConfigurationModule();

            if ( ! StringUtil.equals(goAppConfig.workingDir, project.getBasePath()) ||
                 ! StringUtil.equals(goAppConfig.scriptName, name) ||
                 ! (configurationModule != null && module != null && module.equals(configurationModule.getModule())))
                return false;

            return true;
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

        if (((GoFile) file).getMainFunction() == null) {
            return false;
        }

        try {
            VirtualFile virtualFile = file.getVirtualFile();
            if (virtualFile == null) {
                return false;
            }

            Project project = file.getProject();
            Module module = ProjectRootManager.getInstance(project).getFileIndex().getModuleForFile(virtualFile);
            String name = file.getVirtualFile().getCanonicalPath();


            ((GoApplicationConfiguration) configuration).workingDir = project.getBasePath();
            ((GoApplicationConfiguration) configuration).goOutputDir = project.getBasePath();

            //Append bin-folder, if it exists
            if(new File(project.getBasePath(), "bin").exists()) {
                ((GoApplicationConfiguration) configuration).goOutputDir += File.separatorChar + "bin";
            }
            ((GoApplicationConfiguration) configuration).goBuildBeforeRun = true;
            ((GoApplicationConfiguration) configuration).runBuilderArguments = "";
            ((GoApplicationConfiguration) configuration).scriptName = name;
            ((GoApplicationConfiguration) configuration).scriptArguments = "";

            ((GoApplicationConfiguration) configuration).autoStartGdb = true;
            ((GoApplicationConfiguration) configuration).GDB_PATH = "gdb";
            ((GoApplicationConfiguration) configuration).debugBuilderArguments = "-gcflags \"-N -I\"";


            ((GoApplicationConfiguration) configuration).setModule(module);
            configuration.setName(((GoApplicationConfiguration) configuration).suggestedName());

            return true;
        } catch (Exception ex) {
            LOG.error(ex);
        }

        return false;
    }

    public PsiElement getSourceElement() {
        return element;
    }

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

        RunnerAndConfigurationSettings settings = RunManagerEx.getInstanceEx(project).createRunConfiguration("", getConfigurationFactory());
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

    protected RunnerAndConfigurationSettings findExistingByElement(Location location,
                                                                   @NotNull List<RunnerAndConfigurationSettings> existingConfigurations) {
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
