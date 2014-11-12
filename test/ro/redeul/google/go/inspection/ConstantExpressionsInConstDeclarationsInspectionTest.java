package ro.redeul.google.go.inspection;

import org.junit.Ignore;

public class ConstantExpressionsInConstDeclarationsInspectionTest
    extends GoInspectionTestCase {

//    public void testMandatoryFirstExpressions() throws Exception { _testSingleCompletion(); }
//    public void testLessExpressionsThanIdentifiers() throws Exception { _testSingleCompletion(); }
    public void testConstantExpressions() throws Exception { doTest(); }
    public void testNonConstantExpressions() throws Exception { doTest(); }

    public void testIssue874() throws Exception { doTest(); }
}
