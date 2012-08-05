package ro.redeul.google.go.completion;

public class GoFunctionsCompletionTestCase extends GoCompletionTestCase{
    protected String getTestDataRelativePath() {
        return super.getTestDataRelativePath() + "functions";
    }

    public void testLocalFunctionVariants() {
        doTestVariants();
    }
}
