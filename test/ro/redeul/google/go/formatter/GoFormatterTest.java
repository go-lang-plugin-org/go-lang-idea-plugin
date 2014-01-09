package ro.redeul.google.go.formatter;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import ro.redeul.google.go.GoEditorAwareTestCase;
import ro.redeul.google.go.lang.psi.GoFile;
import org.junit.Ignore;

import static ro.redeul.google.go.util.EditorUtil.reformatPositions;

public class GoFormatterTest extends GoEditorAwareTestCase {
    public void testTopLevel() throws Exception { doTest(); }
    public void testBinaryExpressions() throws Exception { doTest(); }
    public void testAssignment() throws Exception { 
        // TODO: Fix Formatter Test
        // doTest(); 
    }
    public void testType() throws Exception { doTest(); }
    public void testCastFunc() throws Exception { doTest(); }
    public void testFunctionCall() throws Exception { 
        // TODO: Fix Formatter Test
        // doTest(); 
    }
    public void testInterfaceType() throws Exception { doTest(); }
    public void testCallParameters() throws Exception { doTest(); }
    public void testComments() throws Exception { doTest(); }
    public void testComposites() throws Exception { doTest(); }
    public void testCrlf() throws Exception { doTest(); }
    public void testSlices1() throws Exception { doTest(); }
    public void testSlices2() throws Exception { doTest(); }
    public void testFunctionDeclaration() throws Exception { doTest(); }
    // @TODO These tests are not passing yet due to refactoring done automatically by gofmt
    /*
    public void testImport() throws Exception { doTest(); }
    public void testTypeswitch() throws Exception { doTest(); }
    */

    public void testIssue_gh255() throws  Exception { doTest(); }
    public void testIssue_gh373() throws  Exception { doTest(); }

    @Override
    protected void invoke(Project project, Editor editor, GoFile file) {
        reformatPositions(file, 0, file.getTextLength());
    }

    @Override
    protected String getTestDataRelativePath() {
        return "formatter/";
    }
}
