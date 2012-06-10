package ro.redeul.google.go.inspection;

import java.util.ArrayList;
import java.util.List;

import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.ProblemDescriptor;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionDeclaration;

public class FunctionDeclarationInspectionTest extends AbstractProblemDescriptionBasedTest {
    public void testDuplicateArg() throws Exception{ doTest(); }
    public void testWithoutReturn() throws Exception{ doTest(); }
    public void testRedeclaredParameterInResultList() throws Exception{ doTest(); }
    public void testReturnParameterCountDismatch() throws Exception{ doTest(); }
    public void testVariadic() throws Exception{ doTest(); }

    @Override
    protected String getInspectionName() {
        return "functionDeclaration";
    }

    @Override
    protected List<ProblemDescriptor> detectProblems(GoFile file, InspectionManager inspectionManager) {
        List<ProblemDescriptor> problems = new ArrayList<ProblemDescriptor>();
        for (GoFunctionDeclaration fd : file.getFunctions()) {
            FunctionDeclarationInspection fdi = new FunctionDeclarationInspection(inspectionManager, fd);
            problems.addAll(fdi.checkFunction());
        }

        return problems;
    }
}
