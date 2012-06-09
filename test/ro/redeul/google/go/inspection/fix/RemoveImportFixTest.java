package ro.redeul.google.go.inspection.fix;

import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.DebugUtil;
import ro.redeul.google.go.FileDataBasedTest;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.toplevel.GoImportDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoImportDeclarations;

public class RemoveImportFixTest extends FileDataBasedTest {
    public void testSimple() throws Exception{ doTest(); }
    public void testMultiLine1() throws Exception{ doTest(); }
    public void testMultiLine2() throws Exception{ doTest(); }
    public void testMultiLine3() throws Exception{ doTest(); }

    @Override
    protected void invoke(Project project, Editor editor, GoFile file) {
        PsiElement element = file.findElementAt(editor.getSelectionModel().getSelectionStart());
        if (!(element instanceof GoImportDeclaration)) {
            element = element.getParent();
        }

        if (element instanceof GoImportDeclarations) {
            element = ((GoImportDeclarations) element).getDeclarations()[0];
        }

        assertTrue(element instanceof GoImportDeclaration);
        System.out.println(DebugUtil.psiToString(file, false, true));
        InspectionManager im = InspectionManager.getInstance(project);
        LocalQuickFix fix = null;
        ProblemDescriptor pd = im.createProblemDescriptor(element, "", fix, ProblemHighlightType.ERROR, true);
        new RemoveImportFix(element).applyFix(project, pd);
    }

    @Override
    protected String getTestDataRelativePath() {
        return "inspection/fix/removeImport/";
    }
}
