package ro.redeul.google.go.completion;

import java.io.IOException;

public class GoCompletionFunctionsTestCase extends GoCompletionTestCase{
    protected String getTestDataRelativePath() {
        return super.getTestDataRelativePath() + "functions";
    }

    public void testLocalFunctionVariants() throws IOException {
        _testVariants();
    }

    public void testFunctionParameterType() throws IOException {
        _testSingleCompletion();
    }

    public void testInterfaceFunctionParameterType() throws IOException {
        _testSingleCompletion();
    }
}
