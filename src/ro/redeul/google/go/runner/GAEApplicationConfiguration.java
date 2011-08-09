package ro.redeul.google.go.runner;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.Executor;
import com.intellij.execution.RunConfigurationExtension;
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
import com.intellij.openapi.util.DefaultJDOMExternalizer;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.WriteExternalException;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.PathUtil;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.runner.ui.GAERunConfigurationEditorForm;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: mtoader
 * Date: Aug 19, 2010
 * Time: 2:53:03 PM
 * To change this template use File | Settings | File Templates.
 */
public class GAEApplicationConfiguration extends ModuleBasedConfiguration<GoApplicationModuleBasedConfiguration> {

    public String sdkDirectory;
    public String email;
    public String password;
    public String scriptArguments;
    public String workDir;

    public GAEApplicationConfiguration(String name, Project project, GAERunConfigurationType configurationType) {
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
        return new GAEApplicationConfiguration(getName(), getProject(), GAERunConfigurationType.getInstance());
    }

    public SettingsEditor<? extends RunConfiguration> getConfigurationEditor() {
        return new GAERunConfigurationEditorForm(getProject());
    }

    public void readExternal(final Element element) throws InvalidDataException {
        PathMacroManager.getInstance(getProject()).expandPaths(element);
        super.readExternal(element);
        RunConfigurationExtension.readSettings(this, element);
        DefaultJDOMExternalizer.readExternal(this, element);
        readModule(element);
//        EnvironmentVariablesComponent.readExternal(element, getEnvs());
    }

    public void writeExternal(final Element element) throws WriteExternalException {
        super.writeExternal(element);
        RunConfigurationExtension.writeSettings(this, element);
        DefaultJDOMExternalizer.writeExternal(this, element);
        writeModule(element);
//        EnvironmentVariablesComponent.writeExternal(element, getEnvs());
        PathMacroManager.getInstance(getProject()).collapsePathsRecursively(element);
    }

    public RunProfileState getState(@NotNull Executor executor, @NotNull ExecutionEnvironment env) throws ExecutionException {
        GAERunningState state = new GAERunningState(env,sdkDirectory,scriptArguments,workDir);
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
