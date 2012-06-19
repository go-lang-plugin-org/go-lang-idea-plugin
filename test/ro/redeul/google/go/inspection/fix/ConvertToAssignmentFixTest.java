package ro.redeul.google.go.inspection.fix;

import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import ro.redeul.google.go.GoEditorAwareTestCase;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;

import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.findParentOfType;

public class ConvertToAssignmentFixTest extends GoEditorAwareTestCase {
    public void testSimple() throws Exception{ doTest(); }

    @Override
    protected void invoke(Project project, Editor editor, GoFile file) {
        PsiElement element = file.findElementAt(editor.getSelectionModel().getSelectionStart());
        GoLiteralIdentifier identifier = findParentOfType(element, GoLiteralIdentifier.class);
        assertNotNull(identifier);
        InspectionManager im = InspectionManager.getInstance(project);
        LocalQuickFix fix = null;
        ProblemDescriptor pd = im.createProblemDescriptor(identifier, "", fix, ProblemHighlightType.ERROR, true);
        new ConvertToAssignmentFix().applyFix(project, pd);
    }

    @Override
    protected String getTestDataRelativePath() {
        return "fixes/convertToAssignment/";
    }
}
