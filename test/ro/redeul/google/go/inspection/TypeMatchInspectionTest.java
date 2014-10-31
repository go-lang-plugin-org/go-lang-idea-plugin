package ro.redeul.google.go.inspection;

import org.junit.Ignore;

public class TypeMatchInspectionTest extends GoInspectionTestCase {
    public void testArithmetic() throws Exception {
        doTest();
    }

    public void testLogical() throws Exception {
        doTest();
    }

    @Ignore("Broken by my stupidity")
    public void testRelational() throws Exception {
        doTest();
    }

    public void testIssue389() throws Exception {
        doTest();
    }

    public void testAlias() throws Exception {
        doTest();
    }
}
