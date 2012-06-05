package ro.redeul.google.go.inspection;

import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.QuickFix;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import ro.redeul.google.go.FileDataBasedTest;
import ro.redeul.google.go.lang.psi.GoFile;

public class UnusedImportInspectionTest extends FileDataBasedTest {
    public void testSimple() throws Exception{ doTest(); }
    public void testOnlyOneImport() throws Exception{ doTest(); }
    public void testBlankImport() throws Exception{ doTest(); }

    @Override
    protected void invoke(Project project, Editor myEditor, GoFile file) {
        InspectionManager im = InspectionManager.getInstance(project);
        for (ProblemDescriptor pd : new UnusedImportInspection().doCheckFile(file, im)) {
            QuickFix[] fixes = pd.getFixes();
            assertEquals(1, fixes.length);
            fixes[0].applyFix(project, pd);
        }
    }

    @Override
    protected String getTestDataRelativePath() {
        return "inspection/unusedImport/";
    }
}
