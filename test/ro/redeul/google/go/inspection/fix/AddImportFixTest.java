package ro.redeul.google.go.inspection.fix;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import org.junit.Ignore;
import ro.redeul.google.go.GoEditorAwareTestCase;
import ro.redeul.google.go.lang.psi.GoFile;

public class AddImportFixTest extends GoEditorAwareTestCase {
    public void testSimple() throws Exception{ doTest(); }
    public void testOneExistingImport() throws Exception{ doTest(); }
    public void testMultipleExistingImport() throws Exception{ doTest(); }

    @Ignore("not ready yet")
    public void testDotImport() throws Exception{ doTest(); }

    @Override
    protected void invoke(Project project, Editor editor, GoFile file) {
        int start = editor.getSelectionModel().getSelectionStart();
        int end = editor.getSelectionModel().getSelectionEnd();
        String pathToImport = editor.getDocument().getText(new TextRange(start, end));
        AddImportFix.addImport(file, editor, pathToImport);
    }

    @Override
    protected String getTestDataRelativePath() {
        return "fixes/addImport/";
    }
}
