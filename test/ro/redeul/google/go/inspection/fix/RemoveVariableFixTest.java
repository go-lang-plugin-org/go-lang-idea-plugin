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
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;

public class RemoveVariableFixTest extends FileDataBasedTest {
    public void testSimple() throws Exception{ doTest(); }
    public void testShortVarAndBlank1() throws Exception{ doTest(); }
    public void testShortVarAndBlank2() throws Exception{ doTest(); }
    public void testShortOnlyOneVar1() throws Exception{ doTest(); }
    public void testShortOnlyOneVar2() throws Exception{ doTest(); }

    public void testVarAndBlank1() throws Exception{ doTest(); }
    public void testVarAndBlank2() throws Exception{ doTest(); }
    public void testVarAndBlank3() throws Exception{ doTest(); }
    public void testOnlyOneVar1() throws Exception{ doTest(); }
    public void testOnlyOneVar2() throws Exception{ doTest(); }
    public void testOnlyOneVarWithType1() throws Exception{ doTest(); }
    public void testOnlyOneVarWithType2() throws Exception{ doTest(); }

    public void testMultiLine1() throws Exception{ doTest(); }
    public void testMultiLine2() throws Exception{ doTest(); }

    public void testConstSimple() throws Exception{ doTest(); }
    public void testConstMultiLine1() throws Exception{ doTest(); }
    public void testConstMultiLine2() throws Exception{ doTest(); }


    @Override
    protected void invoke(Project project, Editor editor, GoFile file) {
        PsiElement element = file.findElementAt(editor.getSelectionModel().getSelectionStart());
        if (!(element instanceof GoLiteralIdentifier)) {
            element = element.getParent();
        }
        assertTrue(element instanceof GoLiteralIdentifier);
        System.out.println(DebugUtil.psiToString(file, false, true));
        InspectionManager im = InspectionManager.getInstance(project);
        LocalQuickFix fix = null;
        ProblemDescriptor pd = im.createProblemDescriptor(element, "", fix, ProblemHighlightType.ERROR, true);
        new RemoveVariableFix().applyFix(project, pd);
    }

    @Override
    protected String getTestDataRelativePath() {
        return "inspection/fix/removeVariable/";
    }
}
