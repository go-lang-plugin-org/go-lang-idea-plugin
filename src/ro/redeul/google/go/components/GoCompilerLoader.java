package ro.redeul.google.go.components;

import java.util.Arrays;
import java.util.HashSet;

import com.intellij.compiler.CompilerWorkspaceConfiguration;
import com.intellij.openapi.compiler.CompilerManager;
import com.intellij.openapi.components.AbstractProjectComponent;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.GoFileType;
import ro.redeul.google.go.compilation.GoInstallCompiler;
import ro.redeul.google.go.compilation.GoCompiler;
import ro.redeul.google.go.compilation.GoMakefileCompiler;
import ro.redeul.google.go.ide.GoModuleType;
import ro.redeul.google.go.ide.GoProjectSettings;

/**
 * @author Mihai Claudiu Toader <mtoader@gmail.com>
 *         Date: Aug 24, 2010
 */
public class GoCompilerLoader extends AbstractProjectComponent {

    private boolean myPreviousExternalCompilerSetting = false;
    private boolean myProjectUsesGo = false;

    private CompilerWorkspaceConfiguration myConfiguration;

    public GoCompilerLoader(Project project) {
        super(project);
    }

    @NotNull
    public String getComponentName() {
        return "GoCompilerLoader";
    }

    public void projectOpened() {
        myConfiguration = CompilerWorkspaceConfiguration.getInstance(myProject);


        ModuleManager moduleManager = ModuleManager.getInstance(myProject);
        for (Module module : moduleManager.getModules()) {
            if (ModuleType.get(module) == GoModuleType.getInstance())
                myProjectUsesGo = true;
        }

        if (myProjectUsesGo) {
            myPreviousExternalCompilerSetting = myConfiguration.USE_COMPILE_SERVER;
            myConfiguration.USE_COMPILE_SERVER = false;
        }

        CompilerManager compilerManager =
            CompilerManager.getInstance(myProject);

        for (GoCompiler compiler : getCompilerManager().getCompilers(
            GoCompiler.class)) {
            getCompilerManager().removeCompiler(compiler);
        }

        for (GoMakefileCompiler compiler : getCompilerManager().getCompilers(
            GoMakefileCompiler.class)) {
            getCompilerManager().removeCompiler(compiler);
        }

        for (GoInstallCompiler compiler : getCompilerManager().getCompilers(
                GoInstallCompiler.class)) {
            getCompilerManager().removeCompiler(compiler);
        }

        compilerManager.addCompilableFileType(GoFileType.INSTANCE);

        switch (GoProjectSettings.getInstance(myProject)
                                 .getState().BUILD_SYSTEM_TYPE) {
            case Internal:
                compilerManager.addCompiler(new GoCompiler(myProject));
                break;
            case Makefile:
                compilerManager.addTranslatingCompiler(
                    new GoMakefileCompiler(myProject),
                    new HashSet<FileType>(Arrays.asList(GoFileType.INSTANCE)),
                    new HashSet<FileType>(Arrays.asList(FileType.EMPTY_ARRAY)));
                break;
            case Install:
                compilerManager.addTranslatingCompiler(
                        new GoInstallCompiler(myProject),
                        new HashSet<FileType>(Arrays.asList(GoFileType.INSTANCE)),
                        new HashSet<FileType>(Arrays.asList(FileType.EMPTY_ARRAY)));
        }
    }

    private CompilerManager getCompilerManager() {
        return CompilerManager.getInstance(myProject);
    }

    @Override
    public void projectClosed() {
        if (myProjectUsesGo) {
            myConfiguration.USE_COMPILE_SERVER = myPreviousExternalCompilerSetting;
        }
    }
}
