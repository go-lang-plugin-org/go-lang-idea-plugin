package ro.redeul.google.go.inspection.fix;

import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import ro.redeul.google.go.GoEditorAwareTestCase;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.statements.GoReturnStatement;

import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.findParentOfType;

public class ChangeReturnsParametersFixTest extends GoEditorAwareTestCase {

    public void testTest1() throws Exception {
        doTest();
    }

    public void testTest2() throws Exception {
        doTest();
    }

    public void testTest3() throws Exception {
        doTest();
    }

    public void testTest4() throws Exception {
        doTest();
    }

    @Override
    protected void invoke(final Project project, final Editor editor, final GoFile file) {
        PsiElement element = file.findElementAt(editor.getSelectionModel().getSelectionStart());


        final GoReturnStatement expression;
        if (element.getParent() instanceof GoReturnStatement) {
            expression = (GoReturnStatement) element.getParent();
        } else {
            expression = element instanceof GoReturnStatement ?
                    (GoReturnStatement) element :
                    findParentOfType(element, GoReturnStatement.class);
        }

        assertNotNull(expression);

        CommandProcessor.getInstance().executeCommand(project, new Runnable() {
            @Override
            public void run() {
                new ChangeReturnsParametersFix(expression).invoke(project, file, editor, expression, expression);
            }
        }, "", null);
    }

    @Override
    protected String getTestDataRelativePath() {
        return "fixes/changeReturnsParameters/";
    }
}
