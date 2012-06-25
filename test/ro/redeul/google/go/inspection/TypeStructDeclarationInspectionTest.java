package ro.redeul.google.go.inspection;

public class TypeStructDeclarationInspectionTest extends GoInspectionTestCase {
    public void testDuplicateFields() throws Exception { doTest(); }
    public void testInvalidRecursiveType() throws Exception { doTest(); }
}
