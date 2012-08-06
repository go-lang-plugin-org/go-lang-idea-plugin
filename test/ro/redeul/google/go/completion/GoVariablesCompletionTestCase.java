package ro.redeul.google.go.completion;

public class GoVariablesCompletionTestCase extends GoCompletionTestCase{
    protected String getTestDataRelativePath() {
        return super.getTestDataRelativePath() + "variables";
    }

    public void testLocalVar() {
        doTest();
    }

    public void testEnclosingScopeVar() {
        doTest();
    }

    public void testLocalVarVariants() {
        doTestVariants();
    }

    public void testStructFieldViaChannelExpression() {
        doTestVariants();
    }
}
