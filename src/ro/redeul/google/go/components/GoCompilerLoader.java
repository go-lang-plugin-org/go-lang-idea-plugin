package ro.redeul.google.go.components;

import java.util.Arrays;
import java.util.HashSet;

import com.intellij.openapi.compiler.CompilerManager;
import com.intellij.openapi.components.AbstractProjectComponent;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.GoFileType;
import ro.redeul.google.go.compilation.GoCompiler;
import ro.redeul.google.go.compilation.GoMakefileCompiler;
import ro.redeul.google.go.ide.GoProjectSettings;

/**
 * @author Mihai Claudiu Toader <mtoader@gmail.com>
 *         Date: Aug 24, 2010
 */
public class GoCompilerLoader extends AbstractProjectComponent {

    public GoCompilerLoader(Project project) {
        super(project);
    }


    @NotNull
    public String getComponentName() {
        return "GoCompilerLoader";
    }

    public void projectOpened() {
        CompilerManager compilerManager = CompilerManager.getInstance(myProject);
        compilerManager.addCompilableFileType(GoFileType.INSTANCE);

        switch (GoProjectSettings.getInstance(myProject).getState().BUILD_SYSTEM_TYPE) {
            case Internal:
                compilerManager.addTranslatingCompiler(
                        new GoCompiler(myProject),
                        new HashSet<FileType>(Arrays.asList(GoFileType.INSTANCE)),
                        new HashSet<FileType>(Arrays.asList(FileType.EMPTY_ARRAY)));

                break;
            case Makefile:
                compilerManager.addTranslatingCompiler(
                        new GoMakefileCompiler(myProject),
                        new HashSet<FileType>(Arrays.asList(GoFileType.INSTANCE)),
                        new HashSet<FileType>(Arrays.asList(FileType.EMPTY_ARRAY)));
                break;
        }
    }
}
