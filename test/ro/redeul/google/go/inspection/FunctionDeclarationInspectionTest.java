package ro.redeul.google.go.inspection;

import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.ProblemDescriptor;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionDeclaration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FunctionDeclarationInspectionTest extends AbstractProblemDescriptionBasedTest {
    public void testDuplicateArg() throws Exception{ doTest(); }
    public void testWithoutReturn() throws Exception{ doTest(); }
    public void testRedeclaredParameterInResultList() throws Exception{ doTest(); }
//    public void testReturnParameterQuantityUnmatch() throws Exception{ doTest(); }

    @Override
    protected String getInspectionName() {
        return "functionDeclaration";
    }

    @Override
    protected ProblemDescriptor[] detectProblems(GoFile file, InspectionManager inspectionManager) {
        List<ProblemDescriptor> problems = new ArrayList<ProblemDescriptor>();
        for (GoFunctionDeclaration fd : file.getFunctions()) {
            FunctionDeclarationInspection fdi = new FunctionDeclarationInspection(inspectionManager, fd);
            problems.addAll(Arrays.asList(fdi.checkFunction()));
        }
        return problems.toArray(new ProblemDescriptor[problems.size()]);
    }
}
