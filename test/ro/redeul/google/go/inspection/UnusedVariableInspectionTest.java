package ro.redeul.google.go.inspection;

import org.junit.Ignore;

public class UnusedVariableInspectionTest extends GoInspectionTestCase {
    public void testSimple() throws Exception {
        doTest();
    }

    public void testForScope() throws Exception {
        doTest();
    }

    public void testTypeFields() throws Exception {
        doTest();
    }

    public void testUnusedParameter() throws Exception {
        doTest();
    }

    public void testFunction() throws Exception {
        doTest();
    }

    public void testInterface() throws Exception {
        doTest();
    }

    public void testIota() throws Exception {
        doTest();
    }

    public void testAnonymousFunction() throws Exception {
        doTest();
    }

    public void testAnonymousFunctionWithCall() throws Exception {
        doTest();
    }

    public void testUnusedConst() throws Exception {
        doTest();
    }

    @Ignore("broken by the new resolver")
    public void testReturnVariable() throws Exception {
        doTest();
    }

    public void testCompositeExpressionKey() throws Exception {
        doTest();
    }

    public void testClosuresResultParameterUnsolveBug() throws Exception {
        doTest();
    }

    @Ignore("failing test")
    public void testIssue438() throws Exception {
        doTest();
    }

    @Ignore("failing test")
    public void testIssue865() throws Exception {
        doTest();
    }

}
