package ro.redeul.google.go.runner;

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
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.runner.ui.GoTestConfigurationEditorForm;
import ro.redeul.google.go.runner.ui.properties.GoTestConsoleProperties;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;

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

    public enum TestTargetType {
        CWD, Package, File
    }

    public String envVars = "";
    public String testRunnerArgs = "";
    public TestTargetType testTargetType = TestTargetType.Package;
    public String packageName = "";
    public String packageDir = "";
    public String testFile = "";
    public String testArgs = "";
    public String workingDir = "";
    public Type executeWhat = Type.Test;
    public String filter = "";
    public boolean useShortRun = false;
    public boolean testBeforeBenchmark = false;
    public boolean goVetEnabled = false;

    public GoTestConfiguration(String name, Project project, GoTestConfigurationType configurationType) {
        super(name, new GoApplicationModuleBasedConfiguration(project), configurationType.getConfigurationFactories()[0]);
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
    public void checkConfiguration() throws RuntimeConfigurationException {
        super.checkConfiguration();

        if (testTargetType.equals(TestTargetType.Package) &&
                (packageName == null || packageName.isEmpty())) {
            throw new RuntimeConfigurationException("Package name is required");
        }

        if (testTargetType.equals(TestTargetType.File) &&
                (testFile.isEmpty() || (!testFile.isEmpty() && !testFile.contains("_test.go")))) {
            throw new RuntimeConfigurationException("The selected file does not appear to be a test file");
        }

        if (!workingDir.isEmpty()) {
            File dir = new File(workingDir);

            if (!dir.exists()) {
                throw new RuntimeConfigurationException("The selected application working directory does not appear to exist.");
            }

            if (!dir.isDirectory()) {
                throw new RuntimeConfigurationException("The selected application working directory does not appear to be a directory.");
            }
        }
    }

    @NotNull
    public SettingsEditor<? extends RunConfiguration> getConfigurationEditor() {
        return new GoTestConfigurationEditorForm(getProject());
    }

    public void readExternal(final Element element) throws InvalidDataException {
        PathMacroManager.getInstance(getProject()).expandPaths(element);
        super.readExternal(element);
        readModule(element);
        XmlSerializer.deserializeInto(this, element);
    }

    public void writeExternal(final Element element) throws WriteExternalException {
        super.writeExternal(element);
        XmlSerializer.serializeInto(this, element);
        writeModule(element);
        PathMacroManager.getInstance(getProject()).collapsePathsRecursively(element);
    }

    public RunProfileState getState(@NotNull Executor executor, @NotNull ExecutionEnvironment env)
            throws ExecutionException {
        if (this.workingDir.isEmpty()) {
            this.workingDir = getProject().getBaseDir().getCanonicalPath();
        }

        return new GoCommandLineState(new GoTestConsoleProperties(this, executor), env);
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
