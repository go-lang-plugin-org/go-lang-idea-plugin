package ro.redeul.google.go.runner;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;

import com.intellij.execution.CantRunException;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.CommandLineState;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.configurations.ModuleBasedConfiguration;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.configurations.RuntimeConfigurationException;
import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.components.PathMacroManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.WriteExternalException;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.xmlb.XmlSerializer;
import com.intellij.util.xmlb.annotations.Transient;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.config.sdk.GoSdkData;
import ro.redeul.google.go.runner.ui.GoTestConfigurationEditorForm;
import ro.redeul.google.go.sdk.GoSdkUtil;
import static ro.redeul.google.go.sdk.GoSdkUtil.prependToGoPath;

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
    public String filter;
    public Type executeWhat = Type.Test;
    public boolean useShortRun;

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

    public RunProfileState getState(@NotNull Executor executor, @NotNull ExecutionEnvironment env) throws ExecutionException {

        CommandLineState state = new CommandLineState(env) {

            @NotNull
            @Override
            protected OSProcessHandler startProcess() throws ExecutionException {
                GeneralCommandLine commandLine = new GeneralCommandLine();

                Sdk sdk = GoSdkUtil.getGoogleGoSdkForProject(getProject());
                if ( sdk == null ) {
                    throw new CantRunException("No Go Sdk defined for this project");
                }

                final GoSdkData sdkData = (GoSdkData)sdk.getSdkAdditionalData();
                if ( sdkData == null ) {
                    throw new CantRunException("No Go Sdk defined for this project");
                }

                if ( getModule() == null || getModule().getModuleFile() == null ) {
                    throw new CantRunException("No module selected for this test configuration");
                }

                final VirtualFile moduleFile = getModule().getModuleFile();
                if ( moduleFile == null || moduleFile.getParent() == null) {
                    throw new CantRunException("The module does not have a valid parent folder");
                }

                commandLine.setExePath(sdkData.GO_BIN_PATH + "/go");
                commandLine.addParameter("test");
                commandLine.addParameter("-v");
                if (useShortRun)
                    commandLine.addParameter("-short");

                switch (executeWhat) {
                    case Test:
                        if (filter != null && !filter.isEmpty())
                            commandLine.addParameter("-run=" + filter.trim());
                        break;
                    case Benchmark:
                        String benchmarkName = ".*";

                        if (filter != null && !filter.isEmpty())
                            benchmarkName = filter.trim();

                        commandLine.addParameter("-bench=" + benchmarkName);
                        break;
                }

                commandLine.addParameter(packageName);
                commandLine.setEnvParams(new HashMap<String, String>() {{
                    put("GOPATH", prependToGoPath(moduleFile.getParent().getCanonicalPath()));
                    put("GOROOT", getSdkHomePath(sdkData));
                }});

                return GoApplicationProcessHandler.runCommandLine(commandLine);
            }

            private String getSdkHomePath(GoSdkData sdkData) {
                if (sdkData.GO_HOME_PATH.isEmpty()) {
                    return new File(sdkData.GO_BIN_PATH).getParent();
                }
                return sdkData.GO_HOME_PATH;
            }
        };

        state.setConsoleBuilder(TextConsoleBuilderFactory.getInstance().createBuilder(getProject()));
        return state;
    }

    public Module getModule() {
        return getConfigurationModule().getModule();
    }
}
