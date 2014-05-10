package ro.redeul.google.go.runner;

import com.intellij.execution.CantRunException;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.*;
import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.RunConfigurationWithSuppressedDefaultRunAction;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.openapi.components.PathMacroManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.JDOMExternalizerUtil;
import com.intellij.openapi.util.WriteExternalException;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.config.sdk.GoAppEngineSdkData;
import ro.redeul.google.go.runner.ui.GaeRunConfigurationEditorForm;
import ro.redeul.google.go.sdk.GoSdkUtil;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

/**
 * Author: Jhonny Everson
 * Author: Florin Patan
 * <p/>
 * Date: Aug 19, 2010
 * Time: 2:53:03 PM
 */
public class GaeLocalConfiguration extends ModuleBasedConfiguration<GoApplicationModuleBasedConfiguration>
                                    implements RunConfigurationWithSuppressedDefaultDebugAction, RunConfigurationWithSuppressedDefaultRunAction {

    public String builderArguments;
    public String workingDir;
    public String envVars;
    public String hostname;
    public String port;
    public String adminPort;

    private static final String ID = "Go App Engine Console";
    private static final String TITLE = "Go App Engine Console Output";

    private static ConsoleView consoleView;

    public GaeLocalConfiguration(String name, Project project, GaeAppEngineRunConfigurationType configurationType) {
        super(name, new GoApplicationModuleBasedConfiguration(project), configurationType.getConfigurationFactories()[0]);
    }

    @Override
    public Collection<Module> getValidModules() {
        Module[] modules = ModuleManager.getInstance(getProject()).getModules();
        return Arrays.asList(modules);
    }

    @Override
    protected ModuleBasedConfiguration createInstance() {
        return new GaeLocalConfiguration(getName(), getProject(), GaeAppEngineRunConfigurationType.getInstance());
    }

    @Override
    public void checkConfiguration() throws RuntimeConfigurationException {
        if (hostname == null || hostname.isEmpty()) {
            throw new RuntimeConfigurationError("hostname cannot be empty");
        }

        if (port == null || port.isEmpty()) {
            throw new RuntimeConfigurationError("port cannot be empty");
        } else if (!port.matches("\\d+")) {
            throw new RuntimeConfigurationError("port is not a valid number");
        } else if (Integer.parseInt(port) < 1024) {
            throw new RuntimeConfigurationError("port is below 1024 and you will need special privileges for it");
        }

        if (adminPort == null || adminPort.isEmpty()) {
            throw new RuntimeConfigurationError("admin_port cannot be empty");
        } else if (!adminPort.matches("\\d+")) {
            throw new RuntimeConfigurationError("admin_port is not a valid number");
        } else if (Integer.parseInt(adminPort) < 1024) {
            throw new RuntimeConfigurationError("admin_port is below 1024 and you will need special privileges for it");
        }
    }

    @NotNull
    public SettingsEditor<? extends RunConfiguration> getConfigurationEditor() {
        return new GaeRunConfigurationEditorForm(getProject());
    }

    @Override
    public void readExternal(final Element element) throws InvalidDataException {
        PathMacroManager.getInstance(getProject()).expandPaths(element);
        super.readExternal(element);

        builderArguments = JDOMExternalizerUtil.readField(element, "builderArguments");
        workingDir = JDOMExternalizerUtil.readField(element, "workingDir");
        envVars = JDOMExternalizerUtil.readField(element, "envVars");
        hostname = JDOMExternalizerUtil.readField(element, "hostname");
        port = JDOMExternalizerUtil.readField(element, "port");
        adminPort = JDOMExternalizerUtil.readField(element, "adminPort");

        readModule(element);
    }

    @Override
    public void writeExternal(final Element element) throws WriteExternalException {
        super.writeExternal(element);
        JDOMExternalizerUtil.writeField(element, "builderArguments", builderArguments);
        JDOMExternalizerUtil.writeField(element, "workingDir", workingDir);
        JDOMExternalizerUtil.writeField(element, "envVars", envVars);
        JDOMExternalizerUtil.writeField(element, "hostname", hostname);
        JDOMExternalizerUtil.writeField(element, "port", port);
        JDOMExternalizerUtil.writeField(element, "adminPort", adminPort);
        writeModule(element);
        PathMacroManager.getInstance(getProject()).collapsePathsRecursively(element);
    }

    @Override
    public RunProfileState getState(@NotNull Executor executor, @NotNull ExecutionEnvironment env) throws ExecutionException {
        final Project project = getProject();

        if (this.workingDir.isEmpty()) {
            this.workingDir = project.getBaseDir().getCanonicalPath();
        }

        if (hostname == null || hostname.isEmpty()) {
            hostname = "localhost";
        }

        if (port == null || port.isEmpty()) {
            port = "8080";
        }

        if (adminPort == null || adminPort.isEmpty()) {
            adminPort = "8000";
        }

        CommandLineState state = new CommandLineState(env) {
            @NotNull
            @Override
            protected OSProcessHandler startProcess() throws ExecutionException {
                Sdk sdk = GoSdkUtil.getGoogleGAESdkForProject(getProject());
                if ( sdk == null ) {
                    throw new CantRunException("No Go AppEngine Sdk defined for this project");
                }

                final GoAppEngineSdkData sdkData = (GoAppEngineSdkData)sdk.getSdkAdditionalData();
                if ( sdkData == null ) {
                    throw new CantRunException("No Go AppEngine Sdk defined for this project");
                }

                String goExecName = sdkData.SDK_HOME_PATH + File.separator + "goapp";

                if (GoSdkUtil.isHostOsWindows()) {
                    goExecName = goExecName.concat(".bat");
                }

                String projectDir = project.getBasePath();

                if (projectDir == null) {
                    throw new CantRunException("Could not retrieve the project directory");
                }

                Map<String, String> sysEnv = GoSdkUtil.getExtendedSysEnv(sdkData, "", envVars);
                GeneralCommandLine commandLine = new GeneralCommandLine();

                commandLine.setExePath(goExecName);
                commandLine.addParameter("serve");

                commandLine.addParameter("-host=" + hostname);
                commandLine.addParameter("-port=" + port);
                commandLine.addParameter("-admin_port=" + adminPort);

                if (builderArguments != null && builderArguments.trim().length() > 0) {
                    commandLine.getParametersList().addParametersString(builderArguments);
                }

                commandLine.getEnvironment().putAll(sysEnv);
                commandLine.setWorkDirectory(workingDir);

                return GoApplicationProcessHandler.runCommandLine(commandLine);
            }
        };

        state.setConsoleBuilder(TextConsoleBuilderFactory.getInstance().createBuilder(project));
        state.addConsoleFilters(new GoConsoleFilter(project, project.getBasePath()));
        return state;
    }
}
