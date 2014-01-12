package uk.co.cwspencer.ideagdb.run;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.*;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.RunConfigurationWithSuppressedDefaultRunAction;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.WriteExternalException;
import com.intellij.util.xmlb.XmlSerializer;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.co.cwspencer.ideagdb.debug.go.GoGdbUtil;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;

public class GdbRunConfiguration extends ModuleBasedConfiguration<GdbRunConfigurationModule>
        implements RunConfigurationWithSuppressedDefaultRunAction,
        RunConfigurationWithSuppressedDefaultDebugAction {
    private static final Logger m_log =
            Logger.getInstance("#uk.co.cwspencer.ideagdb.run.GdbRunConfiguration");

    public String GDB_PATH = "gdb";
    public String STARTUP_COMMANDS = "";
    public Boolean autoStartGdb = true;
    public String scriptName = "";
    public String scriptArguments = "";
    public String builderArguments = "";
    public String goOutputDir = "";
    public String workingDir = "";
    public String envVars = "";
    public Boolean goVetEnabled = false;

    public GdbRunConfiguration(String name, Project project, ConfigurationFactory factory) {
        super(name, new GdbRunConfigurationModule(project), factory);
    }

    @Override
    public Collection<Module> getValidModules() {
        Module[] modules = ModuleManager.getInstance(getProject()).getModules();
        return Arrays.asList(modules);
    }

    @Override
    protected ModuleBasedConfiguration createInstance() {
        return new GdbRunConfiguration(getName(), getProject(),
                GoGdbRunConfigurationType.getInstance().getFactory());
    }

    @NotNull
    @Override
    public SettingsEditor<? extends RunConfiguration> getConfigurationEditor() {
        Project project = getProject();
        return new GdbRunConfigurationEditor<GdbRunConfiguration>(project);
    }

    @Nullable
    @Override
    public RunProfileState getState(@NotNull Executor executor, @NotNull ExecutionEnvironment env)
            throws ExecutionException {
        return new GdbRunProfileState(getProject(), env, this);
    }

    @Override
    public void checkConfiguration() throws RuntimeConfigurationException {
        if (scriptName == null || scriptName.length() == 0)
            throw new RuntimeConfigurationException("Please select the file to run.");
        if (goOutputDir == null || goOutputDir.isEmpty()) {
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
        if (!GoGdbUtil.isValidGdbPath(GDB_PATH)){
            throw new RuntimeConfigurationException("Please select a valid path to gdb.");
        }
        if (builderArguments.isEmpty()) {
            builderArguments = "-gcflags \"-N -l\"";
        }

        super.checkConfiguration();
    }

    @Override
    public void readExternal(Element element) throws InvalidDataException {
        super.readExternal(element);
        readModule(element);
        XmlSerializer.deserializeInto(this, element);
    }

    @Override
    public void writeExternal(Element element) throws WriteExternalException {
        super.writeExternal(element);
        writeModule(element);
        XmlSerializer.serializeInto(this, element);
    }
}
