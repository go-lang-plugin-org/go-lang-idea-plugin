package ro.redeul.google.go.inspection.fix;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.testFramework.MockProblemDescriptor;
import org.junit.Ignore;
import ro.redeul.google.go.GoEditorAwareTestCase;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;

public class AddImportFixTest extends GoEditorAwareTestCase {
    public void testSimple() throws Exception {
        doTest();
    }

    public void testOneExistingImport() throws Exception {
        doTest();
    }

    public void testMultipleExistingImport() throws Exception {
        doTest();
    }

    @Override
    protected void invoke(Project project, Editor editor, GoFile file) {
        int start = editor.getSelectionModel().getSelectionStart();
        int end = editor.getSelectionModel().getSelectionEnd();

        PsiElement element = file.findElementAt(start);
        while (element != null && !(element instanceof GoLiteralIdentifier) && element.getStartOffsetInParent() == 0)
            element = element.getParent();

        if (element == null || !(element instanceof GoLiteralIdentifier))
            fail("invalid markup");

        new AddImportFix((GoLiteralIdentifier) element).execute();
    }

    @Override
    protected void doTest() throws Exception {
        addPackage("p1", "p1/p1.go");
        super.doTest();
    }

    @Override
    protected String getTestDataRelativePath() {
        return "fixes/addImport/";
    }
}
