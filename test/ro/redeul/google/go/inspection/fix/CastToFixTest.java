package ro.redeul.google.go.inspection.fix;

import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import ro.redeul.google.go.GoEditorAwareTestCase;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.expressions.primary.GoLiteralExpression;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionDeclaration;
import ro.redeul.google.go.lang.psi.types.GoPsiType;
import ro.redeul.google.go.lang.psi.utils.GoExpressionUtils;

import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.findParentOfType;

public class CastToFixTest extends GoEditorAwareTestCase {
    public void testCastFunc() throws Exception {
        doTest();
    }

    public void testCastSlice() throws Exception {
        doTest();
    }

    public void testCastString() throws Exception {
        doTest();
    }
    public void testCastVariadicsString() throws Exception {
        doTest();
    }

    public void testCastInterface() throws Exception {
        doTest();
    }

    public void testCastEnum() throws Exception {
        doTest();
    }

    public void testCastLiteralInt() throws Exception {
        doTest();
    }


    @Override
    protected void invoke(final Project project, final Editor editor, final GoFile file) {
        PsiElement element = file.findElementAt(editor.getSelectionModel().getSelectionStart());

        final GoLiteralExpression expression = findParentOfType(element, GoLiteralExpression.class);
        assertNotNull(expression);
        assertInstanceOf(expression.getLiteral(), GoLiteralIdentifier.class);

        GoFunctionDeclaration goFunctionDeclaration = GoExpressionUtils.resolveToFunctionDeclaration(expression);
        assertNotNull(goFunctionDeclaration);
        final GoPsiType type = goFunctionDeclaration.getParameters()[1].getType();

        CommandProcessor.getInstance().executeCommand(project, new Runnable() {
            @Override
            public void run() {
                new CastTypeFix(expression, type).invoke(project, file, editor, expression, expression);
            }
        }, "", null);
    }

    @Override
    protected String getTestDataRelativePath() {
        return "fixes/castTo/";
    }
}
