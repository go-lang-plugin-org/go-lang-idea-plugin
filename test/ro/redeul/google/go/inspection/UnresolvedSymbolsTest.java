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

    public void testNullPointerImportDecl() throws Exception {
        doTest();
    }

    public void testClosuresResultParameterUnsolveBug() throws Exception {
        doTest();
    }

    public void testStructField() throws Exception {
        doTest();
    }

    public void testMethodFromAnotherPackage() throws Exception{
        addPackage("p1", "p1/p1.go");
        doTest();
    }

    public void testIssue858() throws Exception {
        doTest();
    }

    public void testIssue979() throws Exception {
        doTest();
    }

    public void testCallOrConvExpressResolveToConv() throws Exception {
        doTest();
    }

    public void testVarDereferenceParsedAsTypeCast() throws Exception {
        doTest();
    }

    @Ignore("failing test")
    public void testError() throws Exception {
        doTest();
    }

    @Ignore("failing test")
    public void testGoTestFiles() throws Exception{
        doTest();
    }
}
