package uk.co.cwspencer.ideagdb.run;

import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationType;
import com.intellij.execution.configurations.ConfigurationTypeUtil;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class GdbRunConfigurationType implements ConfigurationType {
    private final ConfigurationFactory m_factory = new ConfigurationFactory(this) {
        public RunConfiguration createTemplateConfiguration(Project project) {
            return new GdbRunConfiguration("", project, this);
        }

        @Override
        public boolean canConfigurationBeSingleton() {
            return false;
        }
    };

    public static GdbRunConfigurationType getInstance() {
        return ConfigurationTypeUtil.findConfigurationType(GdbRunConfigurationType.class);
    }

    @Override
    public String getDisplayName() {
        return "GDB";
    }

    @Override
    public String getConfigurationTypeDescription() {
        return "GDB debug configuration";
    }

    @Override
    public Icon getIcon() {
        return AllIcons.RunConfigurations.Application;
    }

    @NotNull
    @Override
    public String getId() {
        return "GdbRunConfigurationType";
    }

    @Override
    public ConfigurationFactory[] getConfigurationFactories() {
        return new ConfigurationFactory[]{m_factory};
    }

    public ConfigurationFactory getFactory() {
        return m_factory;
    }
}
