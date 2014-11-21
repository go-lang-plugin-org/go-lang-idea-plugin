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

    public void testCompositeExpressionKey() throws Exception {
        doTest();
    }

    public void testClosuresResultParameterUnsolveBug() throws Exception {
        doTest();
    }

    public void testSamePackageVariableDeclaredInDifferentFile() throws Exception {
        doTest();
    }

    @Ignore("Failing as it needs some care when looking at a parsing corner case: aka is not trivial. See #865")
    public void testVarDereferenceParsedAsTypeCast() throws Exception {
        doTest();
    }
}
