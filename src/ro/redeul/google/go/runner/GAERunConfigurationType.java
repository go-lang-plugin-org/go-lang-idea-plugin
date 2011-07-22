package ro.redeul.google.go.runner;

import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationType;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.openapi.extensions.Extensions;
import com.intellij.openapi.project.Project;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.GoIcons;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: mtoader
 * Date: Aug 19, 2010
 * Time: 2:49:26 PM
 * To change this template use File | Settings | File Templates.
 */
public class GAERunConfigurationType implements ConfigurationType {

    private final GAEFactory myConfigurationFactory;

    public GAERunConfigurationType() {
        myConfigurationFactory = new GAEFactory(this);
    }

    public String getDisplayName() {
        return "Application Engine Server";
    }

    public String getConfigurationTypeDescription() {
        return "Application Engine Server";
    }

    public Icon getIcon() {
        return GoIcons.GAE_ICON_16x16;
    }

    @NonNls
    @NotNull
    public String getId() {
        return "GAEApplicationRunConfiguration";
    }

    public ConfigurationFactory[] getConfigurationFactories() {
        return new ConfigurationFactory[]{myConfigurationFactory};
    }

    public static GAERunConfigurationType getInstance() {
        return ContainerUtil.findInstance(Extensions.getExtensions(CONFIGURATION_TYPE_EP), GAERunConfigurationType.class);
    }

    public static class GAEFactory extends ConfigurationFactory {

        public GAEFactory(ConfigurationType type) {
            super(type);
        }

        public RunConfiguration createTemplateConfiguration(Project project) {
            return new GAEApplicationConfiguration("Application Engine Server", project, getInstance());
        }        
    }
}
