package ro.redeul.google.go.imports;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import org.junit.Ignore;
import ro.redeul.google.go.GoEditorAwareTestCase;
import ro.redeul.google.go.lang.psi.GoFile;

import java.io.IOException;

public class GoImportOptimizerTest extends GoEditorAwareTestCase {

    public void testSimple() throws Exception { doTest(); }

    public void testRemoveWholeStatement() throws Exception { doTest(); }

    public void testNestedLiterals() throws Exception { doTest(); }

    @Override
    protected void invoke(Project project, Editor editor, GoFile file) {
        try {
            addPackage("p1", "p1/p1.go");
            addPackage("p2", "p2/p2.go");
        } catch (IOException e) {
            e.printStackTrace();
        }
        new GoImportOptimizer().processFile(file).run();
    }

    @Override
    protected String getTestDataRelativePath() {
        return "import/importOptimizer/";
    }
}
