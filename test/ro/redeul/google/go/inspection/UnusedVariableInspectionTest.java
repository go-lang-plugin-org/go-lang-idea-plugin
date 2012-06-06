package ro.redeul.google.go.inspection;

import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.ProblemDescriptor;
import ro.redeul.google.go.lang.psi.GoFile;

public class UnusedVariableInspectionTest extends AbstractProblemDescriptionBasedTest {
    public void testSimple() throws Exception{ doTest(); }
    public void testIfScope() throws Exception{ doTest(); }
    public void testForScope() throws Exception{ doTest(); }
    public void testTypeFields() throws Exception{ doTest(); }
    public void testUnusedConst() throws Exception{ doTest(); }
    public void testUnusedParameter() throws Exception{ doTest(); }
    public void testLocalHidesGlobal() throws Exception{ doTest(); }
    public void testFunction() throws Exception{ doTest(); }
    public void testInterface() throws Exception{ doTest(); }
    public void testIota() throws Exception{ doTest(); }

    @Override
    protected String getInspectionName() {
        return "unusedVariable";
    }

    @Override
    protected ProblemDescriptor[] detectProblems(GoFile file, InspectionManager inspectionManager) {
        return new UnusedVariableInspection().doCheckFile(file, inspectionManager);
    }
}
