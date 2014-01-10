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
 * Author: Jhonny Everson
 * <p/>
 * Date: Aug 19, 2010
 * Time: 2:49:26 PM
 */
public class GaeAppEngineRunConfigurationType implements ConfigurationType {

    private final GoAppEngineRunConfigurationFactory myConfigurationFactory;
    public static final String ID = "GaeLocalAppEngineServer";
    public static final String Name = "Go Local AppEngine Server";
    public static final String Description = "Go AppEngine Server";

    public GaeAppEngineRunConfigurationType() {
        myConfigurationFactory = new GoAppEngineRunConfigurationFactory(this);
    }

    public String getDisplayName() {
        return Name;
    }

    public String getConfigurationTypeDescription() {
        return Description;
    }

    public Icon getIcon() {
        return GoIcons.GAE_ICON_16x16;
    }

    @NonNls
    @NotNull
    public String getId() {
        return ID;
    }

    public ConfigurationFactory[] getConfigurationFactories() {
        return new ConfigurationFactory[]{myConfigurationFactory};
    }

    public static GaeAppEngineRunConfigurationType getInstance() {
        return ContainerUtil.findInstance(Extensions.getExtensions(CONFIGURATION_TYPE_EP), GaeAppEngineRunConfigurationType.class);
    }

    public static class GoAppEngineRunConfigurationFactory extends ConfigurationFactory {

        public GoAppEngineRunConfigurationFactory(ConfigurationType type) {
            super(type);
        }

        public RunConfiguration createTemplateConfiguration(Project project) {
            return new GaeLocalConfiguration(Name, project, getInstance());
        }        
    }
}
