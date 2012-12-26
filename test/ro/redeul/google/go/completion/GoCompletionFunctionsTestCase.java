package ro.redeul.google.go.completion;

public class GoCompletionFunctionsTestCase extends GoCompletionTestCase{
    protected String getTestDataRelativePath() {
        return super.getTestDataRelativePath() + "functions";
    }

    public void testLocalFunctionVariants() {
        doTestVariants();
    }

    public void testFunctionParameterType() {
        doTest();
    }

    public void testInterfaceFunctionParameterType() {
        doTest();
    }
}
