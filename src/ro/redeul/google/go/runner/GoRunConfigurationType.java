package ro.redeul.google.go.runner;

import com.intellij.compiler.options.CompileStepBeforeRun;
import com.intellij.compiler.options.CompileStepBeforeRunNoErrorCheck;
import com.intellij.execution.BeforeRunTask;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationType;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.openapi.extensions.Extensions;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.GoIcons;

import javax.swing.*;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: Aug 19, 2010
 * Time: 2:49:26 PM
 */
public class GoRunConfigurationType implements ConfigurationType {

    private final GoFactory myConfigurationFactory;

    public GoRunConfigurationType() {
        myConfigurationFactory = new GoFactory(this);
    }

    public String getDisplayName() {
        return "Go Application";
    }

    public String getConfigurationTypeDescription() {
        return "Go Application";
    }

    public Icon getIcon() {
        return GoIcons.GO_ICON_16x16;
    }

    @NonNls
    @NotNull
    public String getId() {
        return "GoApplicationRunConfiguration";
    }

    public ConfigurationFactory[] getConfigurationFactories() {
        return new ConfigurationFactory[]{myConfigurationFactory};
    }

    public static GoRunConfigurationType getInstance() {
        return ContainerUtil.findInstance(Extensions.getExtensions(CONFIGURATION_TYPE_EP), GoRunConfigurationType.class);
    }

    public static class GoFactory extends ConfigurationFactory {

        public GoFactory(ConfigurationType type) {
            super(type);
        }

        public RunConfiguration createTemplateConfiguration(Project project) {
            return new GoApplicationConfiguration("Go application", project, getInstance());
        }

        @Override
        public void configureBeforeRunTaskDefaults(Key<? extends BeforeRunTask> providerID, BeforeRunTask task) {
            task.setEnabled(false);
        }
    }
}
