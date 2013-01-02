package ro.redeul.google.go.formatter;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import ro.redeul.google.go.GoEditorAwareTestCase;
import ro.redeul.google.go.lang.psi.GoFile;

import static ro.redeul.google.go.util.EditorUtil.reformatPositions;

public class GoFormatterTest extends GoEditorAwareTestCase {
    public void testTopLevel() throws Exception { doTest(); }
    public void testAssignment() throws Exception { doTest(); }
    public void testType() throws Exception { doTest(); }
    public void testFunctionCall() throws Exception { doTest(); }
    public void testInterfaceType() throws Exception { doTest(); }
    public void testCallParameters() throws Exception { doTest(); }

    public void testFunctionDeclaration() throws Exception { doTest(); }

    @Override
    protected void invoke(Project project, Editor editor, GoFile file) {
        reformatPositions(file, 0, file.getTextLength());
    }

    @Override
    protected String getTestDataRelativePath() {
        return "formatter/";
    }
}
