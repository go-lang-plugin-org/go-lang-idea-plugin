package ro.redeul.google.go.inspection;

import org.junit.Ignore;

public class ConstantExpressionsInConstDeclarationsInspectionTest extends GoInspectionTestCase {

    //    public void testMandatoryFirstExpressions() throws Exception { _testSingleCompletion(); }
//    public void testLessExpressionsThanIdentifiers() throws Exception { _testSingleCompletion(); }

    public void testNonConstantExpressions() throws Exception { doTest(); }

    public void testIssue874() throws Exception { doTest(); }

    public void testAndOrOperators() throws Exception {doTest();}

    public void testDivByZero() throws Exception {doTest();}

    public void testComplementOperator() throws Exception {doTest();}

    public void testLen() throws Exception {doTest();}

    public void testComplex() throws Exception {doTest();}

    @Ignore("not implemented completely")
    public void testConstantExpressions() throws Exception { doTest(); }

    @Ignore("not implemented completely")
    public void testConversions() throws Exception {doTest();}

    @Ignore("not implemented completely")
    public void testOverFlow() throws Exception {doTest();}

    @Ignore("not implemented completely")
    public void testUnsafe() throws Exception {doTest();}

}
