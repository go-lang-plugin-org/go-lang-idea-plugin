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
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.JDOMExternalizerUtil;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.openapi.util.WriteExternalException;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.PathUtil;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.runner.ui.GoRunConfigurationEditorForm;

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
    public String workDir;

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
        scriptName = JDOMExternalizerUtil.readField(element, "scriptName");
        scriptArguments = JDOMExternalizerUtil.readField(element, "scriptArguments");
        workDir = JDOMExternalizerUtil.readField(element, "workDir");
        readModule(element);
    }

    public void writeExternal(final Element element) throws WriteExternalException {
        super.writeExternal(element);
        JDOMExternalizerUtil.writeField(element, "scriptName", scriptName);
        JDOMExternalizerUtil.writeField(element, "scriptArguments",scriptArguments);
        JDOMExternalizerUtil.writeField(element, "workDir", workDir);
        writeModule(element);
        PathMacroManager.getInstance(getProject()).collapsePathsRecursively(element);
    }

    public RunProfileState getState(@NotNull Executor executor, @NotNull ExecutionEnvironment env) throws ExecutionException {

        CommandLineState state = new CommandLineState(env) {

            @NotNull
            @Override
            protected OSProcessHandler startProcess() throws ExecutionException {

                GeneralCommandLine commandLine = new GeneralCommandLine();

                String compiledFileName = getCompiledFileName(getModule(), scriptName);
                if (!new File(compiledFileName).exists()) {
                    throw new CantRunException("Cannot find target. Is main function defined in main package?");
                }

                commandLine.setExePath(compiledFileName);
                if (scriptArguments != null && scriptArguments.trim().length() > 0) {
                    commandLine.getParametersList().addParametersString(scriptArguments);
                }
                commandLine.setWorkDirectory(workDir);

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

                    String compiledFileName = CompilerPaths.getModuleOutputPath(module, false)
                                                + "/go-bins/" + relativePath + "/" + file.getNameWithoutExtension();
                    if (SystemInfo.isWindows) {
                        compiledFileName += ".exe";
                    }
                    return compiledFileName;
                }
            }
        }

        return scriptName;
    }

    public Module getModule() {
        return getConfigurationModule().getModule();
    }
}
