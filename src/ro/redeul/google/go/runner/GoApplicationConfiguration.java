package ro.redeul.google.go.runner;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.*;
import com.intellij.execution.executors.DefaultDebugExecutor;
import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.RunConfigurationWithSuppressedDefaultRunAction;
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
import ro.redeul.google.go.runner.ui.GoApplicationConfigurationEditor;
import ro.redeul.google.go.sdk.GoSdkUtil;
import uk.co.cwspencer.ideagdb.run.GoDebugProfileState;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: Aug 19, 2010
 * Time: 2:53:03 PM
 */
public class GoApplicationConfiguration extends ModuleBasedConfiguration<GoApplicationModuleBasedConfiguration>
												implements RunConfigurationWithSuppressedDefaultDebugAction, RunConfigurationWithSuppressedDefaultRunAction {



    public String GDB_PATH = "gdb";
    public String STARTUP_COMMANDS = "";
    public Boolean autoStartGdb = true;
    public String debugBuilderArguments = "";

    public String scriptName = "";
    public String scriptArguments = "";
    public String runBuilderArguments = "";
    public Boolean goBuildBeforeRun = false;
    public String goOutputDir = "";
    public String workingDir = "";
    public String envVars = "";

    public GoApplicationConfiguration(String name, Project project, GoApplicationConfigurationType configurationType) {
        super(name, new GoApplicationModuleBasedConfiguration(project), configurationType.getConfigurationFactories()[0]);
    }

    @Override
    public Collection<Module> getValidModules() {
        Module[] modules = ModuleManager.getInstance(getProject()).getModules();
        return Arrays.asList(modules);
    }

    @Override
    protected ModuleBasedConfiguration createInstance() {
        return new GoApplicationConfiguration(getName(), getProject(), GoApplicationConfigurationType.getInstance());
    }

    @Override
    public void checkConfiguration() throws RuntimeConfigurationException {
        if (scriptName == null || scriptName.length() == 0)
            throw new RuntimeConfigurationException("Please select the file to run.");
        if (goBuildBeforeRun != null &&
                goBuildBeforeRun &&
                (goOutputDir == null || goOutputDir.isEmpty())) {
            throw new RuntimeConfigurationException("Please select the directory for the executable.");
        }
        if (workingDir == null || workingDir.isEmpty()) {
            throw new RuntimeConfigurationException("Please select the application working directory.");
        } else {
            File dir = new File(workingDir);

            if (!dir.exists()) {
                throw new RuntimeConfigurationException("The selected application working directory does not appear to exist.");
            }

            if (!dir.isDirectory()) {
                throw new RuntimeConfigurationException("The selected application working directory does not appear to be a directory.");
            }
        }

        super.checkConfiguration();
    }

    @NotNull
    public SettingsEditor<? extends RunConfiguration> getConfigurationEditor() {
        return new GoApplicationConfigurationEditor(getProject());
    }

    public void readExternal(final Element element) throws InvalidDataException {
        PathMacroManager.getInstance(getProject()).expandPaths(element);
        super.readExternal(element);
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

        Project project = getProject();

        if (this.workingDir.isEmpty()) {
            this.workingDir = project.getBaseDir().getCanonicalPath();
        }

	    //If the user wants to debug the program, we return a different RunProfileState
	    if(executor.getClass().equals(DefaultDebugExecutor.class)) {
		    return new GoDebugProfileState(getProject(), env, this);
	    }

	    //Else run it
        CommandLineState state = new GoRunProfileState(getProject(), env, this);

        state.setConsoleBuilder(TextConsoleBuilderFactory.getInstance().createBuilder(project));
        state.addConsoleFilters(new GoConsoleFilter(project, project.getBasePath()));
        return state;
    }

    @Override
    public String suggestedName() {
        try {
            return scriptName.equals("") ? "go run" : GoSdkUtil.getVirtualFile(scriptName).getNameWithoutExtension();
        } catch (NullPointerException ignored) {
            return "go run";
        }
    }

}
