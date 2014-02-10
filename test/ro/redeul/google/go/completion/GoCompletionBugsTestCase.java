package ro.redeul.google.go.completion;

public class GoCompletionBugsTestCase extends GoCompletionTestCase{
    protected String getTestDataRelativePath() {
        return super.getTestDataRelativePath() + "bugs";
    }

    public void testGH218_MissingTypeSpec() {
        doTestVariants();
    }

    public void testGH530_MissingFunctionName() {
        doTestVariants();
    }

}
