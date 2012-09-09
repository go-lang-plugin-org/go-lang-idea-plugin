package ro.redeul.google.go.inspection.fix;

import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import ro.redeul.google.go.GoEditorAwareTestCase;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;

import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.findParentOfType;

public class CreateTypeFixTest extends GoEditorAwareTestCase {
    public void testSimple() throws Exception { doTest(); }
    public void testPointer() throws Exception { doTest(); }
    public void testFunctionResult() throws Exception { doTest(); }
    public void testFunctionParameter() throws Exception { doTest(); }
    public void testStruct() throws Exception { doTest(); }

    @Override
    protected void invoke(final Project project, final Editor editor, final GoFile file) {
        PsiElement element = file.findElementAt(editor.getSelectionModel().getSelectionStart());
        final GoLiteralIdentifier identifier = findParentOfType(element, GoLiteralIdentifier.class);
        assertNotNull(identifier);

        CommandProcessor.getInstance().executeCommand(project, new Runnable() {
            @Override
            public void run() {
                new CreateTypeFix(identifier).invoke(project, file, editor, identifier, identifier);
            }
        }, "", null);
    }

    @Override
    protected String getTestDataRelativePath() {
        return "fixes/createType/";
    }
}
