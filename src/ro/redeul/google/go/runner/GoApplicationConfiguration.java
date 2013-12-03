package ro.redeul.google.go.runner;

import com.intellij.execution.CantRunException;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.*;
import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.compiler.CompilerPaths;
import com.intellij.openapi.components.PathMacroManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.openapi.util.WriteExternalException;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.PathUtil;
import com.intellij.util.xmlb.XmlSerializer;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.config.sdk.GoSdkData;
import ro.redeul.google.go.ide.GoProjectSettings;
import ro.redeul.google.go.runner.ui.GoRunConfigurationEditorForm;
import ro.redeul.google.go.sdk.GoSdkUtil;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: Aug 19, 2010
 * Time: 2:53:03 PM
 */
public class GoApplicationConfiguration extends ModuleBasedConfiguration<GoApplicationModuleBasedConfiguration> {

    public String scriptName;
    public String scriptArguments;
    private final String workDir;

    public GoApplicationConfiguration(String name, Project project, GoRunConfigurationType configurationType) {
        super(name, new GoApplicationModuleBasedConfiguration(project), configurationType.getConfigurationFactories()[0]);
        workDir = PathUtil.getLocalPath(project.getBaseDir());
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
        super.checkConfiguration();

        if (scriptName == null || scriptName.length() == 0)
            throw new RuntimeConfigurationException("Please select the file to run.");
        if (getModule() == null)
            throw new RuntimeConfigurationException("Please select the module.");
    }

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

                GeneralCommandLine commandLine = new GeneralCommandLine();

                commandLine.setExePath(sdkData.GO_BIN_PATH);
                commandLine.addParameter("run");
                if (scriptArguments != null && scriptArguments.trim().length() > 0) {
                    commandLine.getParametersList().addParametersString(scriptArguments);
                }
                commandLine.addParameter(scriptName);
                VirtualFile moduleFile = getConfigurationModule().getModule().getModuleFile();

                commandLine.getEnvironment().put("GOROOT", getSdkHomePath(sdkData));
                commandLine.getEnvironment().put("GOPATH", GoSdkUtil.prependToGoPath(moduleFile.getParent().getCanonicalPath()));

                return GoApplicationProcessHandler.runCommandLine(commandLine);
            }
        };

        state.setConsoleBuilder(TextConsoleBuilderFactory.getInstance().createBuilder(getProject()));
        return state;
    }

    private String getCompiledFileName(Module module, String scriptName) {
        VirtualFile[] sourceRoots = ModuleRootManager.getInstance(module).getSourceRoots();
        VirtualFile file = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(new File(scriptName));

        if (file == null) {
            for (VirtualFile sourceRoot : sourceRoots) {
                file = sourceRoot.findChild(scriptName);
                if (file != null) {
                    break;
                }
            }
        }

        if (file != null) {
            for (VirtualFile sourceRoot : sourceRoots) {

                if (VfsUtil.isAncestor(sourceRoot, file, true)) {
                    String relativePath = VfsUtil.getRelativePath(file.getParent(), sourceRoot, File.separatorChar);
                    GoProjectSettings setting = GoProjectSettings.getInstance(module.getProject());
                    String compiledFileName;
                    if (setting.getState().BUILD_SYSTEM_TYPE == GoProjectSettings.BuildSystemType.Install) {
                        compiledFileName = module.getProject().getBasePath() + "/bin/" + relativePath;
                    }else{
                        compiledFileName = CompilerPaths.getModuleOutputPath(module, false)
                                                + "/go-bins/" + relativePath + "/" + file.getNameWithoutExtension();
                    }
                    if (SystemInfo.isWindows) {
                        compiledFileName += ".exe";
                    }
                    return compiledFileName;
                }
            }
        }

        return scriptName;
    }

    Module getModule() {
        return getConfigurationModule().getModule();
    }

    private String getSdkHomePath(GoSdkData sdkData) {
        if (sdkData.GO_HOME_PATH.isEmpty()) {
            return new File(sdkData.GO_BIN_PATH).getParent();
        }
        return sdkData.GO_HOME_PATH;
    }
}
