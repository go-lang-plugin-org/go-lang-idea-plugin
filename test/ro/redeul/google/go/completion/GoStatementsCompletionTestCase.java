package ro.redeul.google.go.completion;

public class GoStatementsCompletionTestCase extends GoCompletionTestCase {
    protected String getTestDataRelativePath() {
        return super.getTestDataRelativePath() + "statements";
    }

    public void testConstDeclaration() {
        doTest();
    }

    public void testVarDeclaration() {
        doTest();
    }

    public void testSwitch() {
        doTest();
    }

    public void testFor() {
        doTest();
    }

    public void testIf() {
        doTest();
    }

    public void testGo() {
        doTest();
    }

    public void testDefer() {
        doTest();
    }

    public void testSelect() {
        doTest();
    }
}
