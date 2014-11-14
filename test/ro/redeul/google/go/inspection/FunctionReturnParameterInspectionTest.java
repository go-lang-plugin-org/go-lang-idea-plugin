package ro.redeul.google.go.inspection;

import org.junit.Ignore;

public class FunctionReturnParameterInspectionTest extends GoInspectionTestCase {
    @Ignore("Broken because #992")
    public void testReturnParameterCountMismatch() throws Exception {
        addPackage("io", "io/Writer.go");
        doTest();
    }

    /** This is referenced from #811 */
    @Ignore("Broken because #992")
    public void testWithInterfaceTypes() throws Exception {
        doTest();
    }

    public void testIssue989() throws Exception{
        doTest();
    }
}
