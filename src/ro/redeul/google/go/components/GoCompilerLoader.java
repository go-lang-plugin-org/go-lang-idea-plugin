package ro.redeul.google.go.components;

import com.intellij.openapi.compiler.CompilerManager;
import com.intellij.openapi.components.AbstractProjectComponent;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.StdFileTypes;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.GoFileType;
import ro.redeul.google.go.compilation.GoCompiler;

import java.util.Arrays;
import java.util.HashSet;

/**
 * Created by IntelliJ IDEA.
 * User: mtoader
 * Date: Aug 24, 2010
 * Time: 1:27:29 AM
 * To change this template use File | Settings | File Templates.
 */
public class GoCompilerLoader extends AbstractProjectComponent {

    public GoCompilerLoader(Project project) {
        super(project);
    }

//    public void initComponent() {
//        TODO: insert component initialization logic here
//    }

//    public void disposeComponent() {
    // TODO: insert component disposal logic here
//    }

    @NotNull
    public String getComponentName() {
        return "GoCompilerLoader";
    }

    public void projectOpened() {
        CompilerManager compilerManager = CompilerManager.getInstance(myProject);
        compilerManager.addCompilableFileType(GoFileType.GO_FILE_TYPE);

        compilerManager.addTranslatingCompiler(
                new GoCompiler(myProject),
                new HashSet<FileType>(Arrays.asList(GoFileType.GO_FILE_TYPE)),
                new HashSet<FileType>(Arrays.asList(FileType.EMPTY_ARRAY)));

//        compilerManager.addTranslatingCompiler(new GroovyCompiler(myProject),
//                new HashSet<FileType>(Arrays.asList(GroovyFileType.GROOVY_FILE_TYPE, StdFileTypes.CLASS)),
//                new HashSet<FileType>(Arrays.asList(StdFileTypes.CLASS)));
    }

    public void projectClosed() {
        // called when project is being closed
    }
}
