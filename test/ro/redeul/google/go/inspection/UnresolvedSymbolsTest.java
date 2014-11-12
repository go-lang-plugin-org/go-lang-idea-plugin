package ro.redeul.google.go.inspection;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.internal.runners.JUnit38ClassRunner;
import org.junit.runner.RunWith;

@RunWith(JUnit38ClassRunner.class)
public class UnresolvedSymbolsTest extends GoInspectionTestCase {

    public void testIfScope() throws Exception {
        doTest();
    }

    @Test
    public void testIfScope2() throws Exception {
        doTest();
    }

    @Test
    public void testForWithClause() throws Exception {
        doTest();
    }

    public void testForWithRange() throws Exception {
        doTest();
    }

    public void testIota() throws Exception {
        doTest();
    }

    public void testUndefinedTypeInMethodReceiver() throws Exception {
        doTest();
    }

    public void testCgo() throws Exception {
        doTest();
    }

    public void testCreateFunction() throws Exception {
        doTest();
    }

    public void testConversionToPointerType() throws Exception {
        doTest();
    }

    @Ignore("broken by new resolver")
    public void testNullPointerImportDecl() throws Exception {
        doTest();
    }

    public void testClosuresResultParameterUnsolveBug() throws Exception {
        doTest();
    }

    public void testStructField() throws Exception {
        doTest();
    }

    @Ignore("failing test")
    public void testIssue865() throws Exception {
        doTest();
    }

    public void testIssue858() throws Exception {
        doTestWithDirectory();
    }
}
