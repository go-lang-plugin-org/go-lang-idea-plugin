package ro.redeul.google.go.runner;

import java.util.Arrays;
import java.util.Collection;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.ModuleBasedConfiguration;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.configurations.RuntimeConfigurationException;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.components.PathMacroManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.WriteExternalException;
import com.intellij.util.xmlb.XmlSerializer;
import com.intellij.util.xmlb.annotations.Transient;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.runner.ui.GoTestConfigurationEditorForm;
import ro.redeul.google.go.runner.ui.properties.GoTestConsoleProperties;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: Aug 19, 2010
 * Time: 2:53:03 PM
 */
public class GoTestConfiguration extends ModuleBasedConfiguration<GoApplicationModuleBasedConfiguration> {

    public enum Type {
        Test, Benchmark
    }

    public String packageName;
    public String packageDir;
    public String filter;
    public Type executeWhat = Type.Test;
    public boolean useShortRun;
    public boolean testBeforeBenchmark;

    public GoTestConfiguration(String name, Project project, GoTestConfigurationType configurationType) {
        super(name, new GoApplicationModuleBasedConfiguration(project),
              configurationType.getConfigurationFactories()[0]);
    }

    @Override
    public Collection<Module> getValidModules() {
        Module[] modules = ModuleManager.getInstance(getProject()).getModules();
        return Arrays.asList(modules);
    }

    @Override
    protected ModuleBasedConfiguration createInstance() {
        return new GoTestConfiguration(getName(), getProject(),
                                       GoTestConfigurationType.getInstance());
    }

    @Override
    @Transient
    public void setModule(Module module) {
        super.setModule(module);
    }

    @Override
    public void checkConfiguration() throws RuntimeConfigurationException {
        super.checkConfiguration();

        if (getModule() == null)
            throw new RuntimeConfigurationException("A module is required");

        if (packageName == null || packageName.isEmpty())
            throw new RuntimeConfigurationException("A package is required");
    }

    public SettingsEditor<? extends RunConfiguration> getConfigurationEditor() {
        return new GoTestConfigurationEditorForm(getProject());
    }

    public void readExternal(final Element element) throws InvalidDataException {
        PathMacroManager.getInstance(getProject()).expandPaths(element);
        XmlSerializer.deserializeInto(this, element);
        readModule(element);
    }

    public void writeExternal(final Element element) throws WriteExternalException {
        super.writeExternal(element);
        XmlSerializer.serializeInto(this, element);
        writeModule(element);
        PathMacroManager.getInstance(getProject()).collapsePathsRecursively(element);
    }

    public RunProfileState getState(@NotNull Executor executor, @NotNull ExecutionEnvironment env)
            throws ExecutionException {
        return new GoCommandLineState(new GoTestConsoleProperties(this, executor), env);
    }

    public Module getModule() {
        return getConfigurationModule().getModule();
    }

    @Override
    public boolean isGeneratedName() {
        return true;
    }

    @Override
    public String suggestedName() {
        String name = getName();
        int pos = name.lastIndexOf('.');
        if (pos == -1) {
            return name;
        }
        return name.substring(pos + 1);
    }
}
