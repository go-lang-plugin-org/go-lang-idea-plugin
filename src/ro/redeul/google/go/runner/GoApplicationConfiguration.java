package ro.redeul.google.go.runner;

import com.intellij.execution.CantRunException;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.*;
import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.openapi.components.PathMacroManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.EmptyRunnable;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.WriteExternalException;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.util.PathUtil;
import com.intellij.util.xmlb.XmlSerializer;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.GoIcons;
import ro.redeul.google.go.config.sdk.GoSdkData;
import ro.redeul.google.go.runner.ui.GoRunConfigurationEditorForm;
import ro.redeul.google.go.sdk.GoSdkUtil;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: Aug 19, 2010
 * Time: 2:53:03 PM
 */
public class GoApplicationConfiguration extends ModuleBasedConfiguration<GoApplicationModuleBasedConfiguration> {

    private static final String ID = "Go Console";
    private static final String TITLE = " build";
    private static ConsoleView consoleView;

    public String scriptName = "";
    public String scriptArguments = "";
    public String builderArguments = "";
    public Boolean goBuildBeforeRun = false;
    public String goOutputDir = "";

    public GoApplicationConfiguration(String name, Project project, GoRunConfigurationType configurationType) {
        super(name, new GoApplicationModuleBasedConfiguration(project), configurationType.getConfigurationFactories()[0]);
        String workDir = PathUtil.getLocalPath(project.getBaseDir());
    }

    @Override
    public Collection<Module> getValidModules() {
        Module[] modules = ModuleManager.getInstance(getProject()).getModules();
        return Arrays.asList(modules);
    }

    @Override
    protected ModuleBasedConfiguration createInstance() {
        return new GoApplicationConfiguration(getName(), getProject(), GoRunConfigurationType.getInstance());
    }

    @Override
    public void checkConfiguration() throws RuntimeConfigurationException {
        if (scriptName == null || scriptName.length() == 0)
            throw new RuntimeConfigurationException("Please select the file to run.");
        if (goBuildBeforeRun != null &&
                goBuildBeforeRun &&
                (goOutputDir == null || goOutputDir.equals(""))) {
            throw new RuntimeConfigurationException("Please select the directory for the executable.");
        }
        super.checkConfiguration();
    }

    @NotNull
    public SettingsEditor<? extends RunConfiguration> getConfigurationEditor() {
        return new GoRunConfigurationEditorForm(getProject());
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

        final Project project = getProject();

        CommandLineState state = new CommandLineState(env) {

            @NotNull
            @Override
            protected OSProcessHandler startProcess() throws ExecutionException {

                Sdk sdk = GoSdkUtil.getGoogleGoSdkForProject(getProject());
                if ( sdk == null ) {
                    throw new CantRunException("No Go Sdk defined for this project");
                }

                final GoSdkData sdkData = (GoSdkData)sdk.getSdkAdditionalData();
                if ( sdkData == null ) {
                    throw new CantRunException("No Go Sdk defined for this project");
                }

                VirtualFile scriptFile = GoSdkUtil.getVirtualFile(scriptName);

                String goExecName = sdkData.GO_BIN_PATH;

                String projectDir = project.getBasePath();

                if (projectDir == null) {
                    throw new CantRunException("Could not retrieve the project directory");
                }

                if (!goBuildBeforeRun) {
                    // Just run
                    GeneralCommandLine commandLine = new GeneralCommandLine();

                    commandLine.setExePath(goExecName);
                    commandLine.addParameter("run");
                    if (builderArguments != null && builderArguments.trim().length() > 0) {
                        commandLine.getParametersList().addParametersString(builderArguments);
                    }

                    commandLine.addParameter(scriptName);
                    if (scriptArguments != null && scriptArguments.trim().length() > 0) {
                        commandLine.getParametersList().addParametersString(scriptArguments);
                    }

                    commandLine.getEnvironment().put("GOROOT", getSdkRootPath(sdkData));
                    commandLine.getEnvironment().put("GOPATH", GoSdkUtil.appendToGoPath(projectDir));

                    return GoApplicationProcessHandler.runCommandLine(commandLine);
                }


                // Build and run
                String execName = goOutputDir.concat("/").concat(getProject().getName());

                if (System.getProperty("os.name").toLowerCase().indexOf("win") >= 0) {
                    execName = execName.concat(".exe");
                }

                ProcessHandler processHandler = null;
                try {
                    ToolWindowManager manager = ToolWindowManager.getInstance(project);
                    ToolWindow window = manager.getToolWindow(ID);

                    if (GoApplicationConfiguration.consoleView == null) {
                        GoApplicationConfiguration.consoleView = TextConsoleBuilderFactory.getInstance().createBuilder(project).getConsole();
                    }

                    if (window == null) {
                        window = manager.registerToolWindow(ID, false, ToolWindowAnchor.BOTTOM);

                        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
                        Content content = contentFactory.createContent(consoleView.getComponent(), "", false);
                        window.getContentManager().addContent(content);
                        window.setIcon(GoIcons.GO_ICON_13x13);
                        window.setToHideOnEmptyContent(true);
                        window.setTitle(TITLE);

                    }

                    window.show(EmptyRunnable.getInstance());

                    Map<String,String> sysEnv = System.getenv();
                    String[] goEnv = new String[sysEnv.size() + 2];
                    Iterator it = sysEnv.entrySet().iterator();
                    int i = 0;
                    while (it.hasNext()) {
                        Map.Entry pairs = (Map.Entry)it.next();
                        goEnv[i] = pairs.getKey() + "=" + pairs.getValue();
                        i++;
                    }

                    goEnv[i] = "GOROOT=" + getSdkRootPath(sdkData);
                    i++;
                    goEnv[i] = "GOPATH=" + GoSdkUtil.appendToGoPath(projectDir);

                    String command = String.format(
                            "%s build %s -o %s %s",
                            goExecName,
                            builderArguments,
                            execName,
                            scriptName
                    );

                    Runtime rt = Runtime.getRuntime();
                    Process proc = rt.exec(command, goEnv);
                    OSProcessHandler handler = new OSProcessHandler(proc, null);
                    consoleView.attachToProcess(handler);
                    consoleView.print(String.format("%s%n", command), ConsoleViewContentType.NORMAL_OUTPUT);
                    handler.startNotify();

                    if (proc.waitFor() == 0) {
                        VirtualFileManager.getInstance().syncRefresh();

                        consoleView.print(String.format("%nFinished building project %s%n", execName), ConsoleViewContentType.NORMAL_OUTPUT);
                    } else {
                        consoleView.print(String.format("%nCould build project %s%n", execName), ConsoleViewContentType.ERROR_OUTPUT);
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                    Messages.showErrorDialog(String.format("Error while processing %s build command.", goExecName), "Error on Google Go Plugin");

                    throw new CantRunException(String.format("Error while processing %s build command.", goExecName));
                }

                // Now run the build
                GeneralCommandLine commandLine = new GeneralCommandLine();

                commandLine.setExePath(execName);
                commandLine.setWorkDirectory(goOutputDir);
                if (scriptArguments != null && scriptArguments.trim().length() > 0) {
                    commandLine.getParametersList().addParametersString(scriptArguments);
                }

                return GoApplicationProcessHandler.runCommandLine(commandLine);
            }
        };

        state.setConsoleBuilder(TextConsoleBuilderFactory.getInstance().createBuilder(project));
        return state;
    }

    private String getSdkRootPath(GoSdkData sdkData) {
        if (sdkData.GO_HOME_PATH.isEmpty()) {
            File possibleRoot = new File(sdkData.GO_BIN_PATH).getParentFile();
            try {
                if (new File(possibleRoot.getCanonicalPath().concat("/src")).exists()) {
                    return possibleRoot.getCanonicalPath();
                }
            } catch (IOException ignored) {
                return "";
            }

            try {
                return possibleRoot.getParentFile().getCanonicalPath();
            } catch (IOException e) {
                return "";
            }
        }
        return sdkData.GO_HOME_PATH;
    }
}
