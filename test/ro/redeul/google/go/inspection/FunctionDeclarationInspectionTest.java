package ro.redeul.google.go.inspection;

public class FunctionDeclarationInspectionTest extends GoInspectionTestCase {
    public void testDuplicateArg() throws Exception{ doTest(); }
    public void testWithoutReturn() throws Exception{ doTest(); }
    public void testRedeclaredParameterInResultList() throws Exception{ doTest(); }
    public void testReturnParameterCountDismatch() throws Exception{ doTest(); }
    public void testVariadic() throws Exception{ doTest(); }
}
