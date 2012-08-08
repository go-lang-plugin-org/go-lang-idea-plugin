package ro.redeul.google.go.completion;

public class GoMethodsCompletionTestCase extends GoCompletionTestCase{
    protected String getTestDataRelativePath() {
        return super.getTestDataRelativePath() + "methods";
    }

    public void testLocalInheritedMethodsVariants() {
        doTestVariants();
    }
}
