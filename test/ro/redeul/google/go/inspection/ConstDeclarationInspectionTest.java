package ro.redeul.google.go.inspection;

public class ConstDeclarationInspectionTest extends GoInspectionTestCase {

    public void testMandatoryFirstExpressions() throws Exception { doTest(); }
    public void testLessExpressionsThanIdentifiers() throws Exception { doTest(); }
    public void testMoreExpressionsThanIdentifiers() throws Exception { doTest(); }
}
