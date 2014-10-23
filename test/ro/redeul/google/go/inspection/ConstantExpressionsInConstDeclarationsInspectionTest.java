package ro.redeul.google.go.inspection;

public class ConstantExpressionsInConstDeclarationsInspectionTest
    extends GoInspectionTestCase {

//    public void testMandatoryFirstExpressions() throws Exception { _testSingleCompletion(); }
//    public void testLessExpressionsThanIdentifiers() throws Exception { _testSingleCompletion(); }
    public void testConstantExpressions() throws Exception { doTest(); }
    public void testNonConstantExpressions() throws Exception { doTest(); }
}
