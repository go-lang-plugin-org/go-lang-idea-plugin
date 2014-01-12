package uk.co.cwspencer.ideagdb.run;

import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationType;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.openapi.extensions.Extensions;
import com.intellij.openapi.project.Project;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.GoIcons;

import javax.swing.*;

public class GoGdbRunConfigurationType implements ConfigurationType {
    private final ConfigurationFactory m_factory = new ConfigurationFactory(this) {
        public RunConfiguration createTemplateConfiguration(Project project) {
            return new GdbRunConfiguration("", project, this);
        }

        @Override
        public boolean canConfigurationBeSingleton() {
            return false;
        }
    };

    public static GoGdbRunConfigurationType getInstance() {
        return ContainerUtil.findInstance(Extensions.getExtensions(CONFIGURATION_TYPE_EP), GoGdbRunConfigurationType.class);
    }

    @Override
    public String getDisplayName() {
        return "Go Debug";
    }

    @Override
    public String getConfigurationTypeDescription() {
        return "Go debug configuration";
    }

    @Override
    public Icon getIcon() {
        return GoIcons.GO_ICON_16x16;
    }

    @NotNull
    @Override
    public String getId() {
        return "GoGdbRunConfigurationType";
    }

    @Override
    public ConfigurationFactory[] getConfigurationFactories() {
        return new ConfigurationFactory[]{m_factory};
    }

    public ConfigurationFactory getFactory() {
        return m_factory;
    }
}
