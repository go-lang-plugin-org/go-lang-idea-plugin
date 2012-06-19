package ro.redeul.google.go.inspection.fix;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import ro.redeul.google.go.GoEditorAwareTestCase;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.GoPsiElement;

import static com.intellij.psi.util.PsiTreeUtil.findElementOfClassAtRange;

public class DeleteStmtFixTest extends GoEditorAwareTestCase {
    public void testSimple() throws Exception{ doTest(); }
    public void testOnlyOneVar() throws Exception{ doTest(); }
    public void testOnlyOneConst() throws Exception{ doTest(); }


    @Override
    protected void invoke(Project project, Editor editor, GoFile file) {
        int start = editor.getSelectionModel().getSelectionStart();
        int end = editor.getSelectionModel().getSelectionEnd();
        GoPsiElement element = findElementOfClassAtRange(file, start, end, GoPsiElement.class);
        DeleteStmtFix.deleteStatement(element);
    }

    @Override
    protected String getTestDataRelativePath() {
        return "fixes/deleteStmt/";
    }
}
