package ro.redeul.google.go.lang.completion.smartEnter;

import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import ro.redeul.google.go.GoEditorAwareTestCase;
import ro.redeul.google.go.lang.psi.GoFile;

public class GoSmartEnterProcessorTest extends GoEditorAwareTestCase {
    public void testFuncInEmptyParameter() throws Exception { doTest(); }
    public void testFuncInParameter() throws Exception { doTest(); }
    public void testFuncInResult() throws Exception { doTest(); }

    public void testFuncInGoParameter() throws Exception { doTest(); }
    public void testFuncInGoEnd() throws Exception { doTest(); }

    public void testFuncInDeferParameter() throws Exception { doTest(); }
    public void testFuncInDeferEnd() throws Exception { doTest(); }

    public void testFuncInLiteralParameter() throws Exception { doTest(); }
    public void testFuncInLiteralEnd() throws Exception { doTest(); }

    public void testIf() throws Exception { doTest(); }

    public void testForClause() throws Exception { doTest(); }
    public void testForCondition() throws Exception { doTest(); }
    public void testForRange() throws Exception { doTest(); }
    public void testForRangeAndVars() throws Exception { doTest(); }
    public void testCaretInFront() throws Exception { doTest(); }

    @Override
    protected void invoke(final Project project, final Editor editor, final GoFile file) {
        CommandProcessor.getInstance().executeCommand(project, new Runnable() {
            @Override
            public void run() {
                new GoSmartEnterProcessor().process(project, editor, file);
            }
        }, "", "");
    }

    @Override
    protected String getTestDataRelativePath() {
        return "completion/smartEnter/";
    }
}
