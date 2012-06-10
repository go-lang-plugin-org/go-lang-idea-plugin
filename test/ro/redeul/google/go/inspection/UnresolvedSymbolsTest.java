package ro.redeul.google.go.inspection;

import java.util.List;

import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.ProblemDescriptor;
import ro.redeul.google.go.lang.psi.GoFile;

public class UnresolvedSymbolsTest extends AbstractProblemDescriptionBasedTest {
    public void testIfScope() throws Exception{ doTest(); }
    public void testForWithClause() throws Exception{ doTest(); }
    public void testForWithRange() throws Exception{ doTest(); }
    public void testIota() throws Exception{ doTest(); }

    @Override
    protected String getInspectionName() {
        return "unresolvedSymbols";
    }

    @Override
    protected List<ProblemDescriptor> detectProblems(GoFile file, InspectionManager inspectionManager) {
        return new UnresolvedSymbols().doCheckFile(file, inspectionManager, false);
    }
}
