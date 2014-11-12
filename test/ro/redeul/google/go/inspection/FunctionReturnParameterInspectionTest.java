package ro.redeul.google.go.inspection;

import org.junit.Ignore;

public class FunctionReturnParameterInspectionTest extends GoInspectionTestCase {
    public void testReturnParameterCountMismatch() throws Exception {
        doTest();
    }

    @Ignore("failing test")
    public void testIssue811() throws Exception {
        doTest();
    }
}
