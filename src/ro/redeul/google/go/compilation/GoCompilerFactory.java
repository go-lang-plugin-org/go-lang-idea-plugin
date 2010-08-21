package ro.redeul.google.go.compilation;

import com.intellij.openapi.compiler.*;
import com.intellij.openapi.compiler.Compiler;
import com.intellij.openapi.project.Project;

/**
 * Created by IntelliJ IDEA.
 * User: mtoader
 * Date: Aug 19, 2010
 * Time: 10:43:49 AM
 * To change this template use File | Settings | File Templates.
 */
public class GoCompilerFactory implements CompilerFactory {

    private Project project;

    public GoCompilerFactory(Project project) {
        this.project = project;
    }

    public Compiler[] createCompilers(CompilerManager compilerManager) {
        return new Compiler[] { new GoCompiler(project) };
    }

}
