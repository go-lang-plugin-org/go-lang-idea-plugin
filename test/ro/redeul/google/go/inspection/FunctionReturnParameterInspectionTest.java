package ro.redeul.google.go.inspection;

public class FunctionReturnParameterInspectionTest extends GoInspectionTestCase {

    public void testReturnParameterCountMismatch() throws Exception {
        addPackage("io", "io/Writer.go");
        doTest();
    }

    public void testWithInterfaceTypes() throws Exception {
        doTest();
    }

    public void testIssue989() throws Exception{
        doTest();
    }

    public void testLen() throws Exception {
        doTest();
    }
}
