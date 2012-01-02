package ro.redeul.google.go.runner;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.ModuleBasedConfiguration;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.configurations.RunProfileState;
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
import com.intellij.openapi.util.WriteExternalException;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.PathUtil;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.runner.ui.GoAppEngineRunConfigurationEditorForm;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;

/**
 * Author: Jhonny Everson
 * <p/>
 * Date: Aug 19, 2010
 * Time: 2:53:03 PM
 */
public class GoAppEngineApplicationConfiguration extends ModuleBasedConfiguration<GoApplicationModuleBasedConfiguration> {

    public String sdkDirectory;
    public String email;
    public String password;
    public String scriptArguments;
    public String workDir;

    public GoAppEngineApplicationConfiguration(String name, Project project, GoAppEngineRunConfigurationType configurationType) {
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
        return new GoAppEngineApplicationConfiguration(getName(), getProject(), GoAppEngineRunConfigurationType.getInstance());
    }

    public SettingsEditor<? extends RunConfiguration> getConfigurationEditor() {
        return new GoAppEngineRunConfigurationEditorForm(getProject());
    }

    public void readExternal(final Element element) throws InvalidDataException {
        PathMacroManager.getInstance(getProject()).expandPaths(element);
        super.readExternal(element);

        sdkDirectory = JDOMExternalizerUtil.readField(element, "sdkDirectory");
        email = JDOMExternalizerUtil.readField(element, "email");
        password = JDOMExternalizerUtil.readField(element, "password");
        scriptArguments = JDOMExternalizerUtil.readField(element, "scriptArguments");
        workDir = JDOMExternalizerUtil.readField(element, "workDir");

        readModule(element);
//        EnvironmentVariablesComponent.readExternal(element, getEnvs());
    }

    public void writeExternal(final Element element) throws WriteExternalException {
        super.writeExternal(element);
        JDOMExternalizerUtil.writeField(element, "sdkDirectory", sdkDirectory);
        JDOMExternalizerUtil.writeField(element, "email", email);
        JDOMExternalizerUtil.writeField(element, "password", password);
        JDOMExternalizerUtil.writeField(element, "scriptArguments", scriptArguments);
        JDOMExternalizerUtil.writeField(element, "workDir", workDir);
        writeModule(element);
//        EnvironmentVariablesComponent.writeExternal(element, getEnvs());
        PathMacroManager.getInstance(getProject()).collapsePathsRecursively(element);
    }

    public RunProfileState getState(@NotNull Executor executor, @NotNull ExecutionEnvironment env) throws ExecutionException {
        GoAppEngineRunningState state = new GoAppEngineRunningState(env,sdkDirectory,scriptArguments,workDir);
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

                    return CompilerPaths.getModuleOutputPath(module, false) + "/go-bins/" + relativePath + "/" +
                            file.getNameWithoutExtension();
                }
            }
        }

        return scriptName;
    }

    public Module getModule() {
        return getConfigurationModule().getModule();
    }
}
