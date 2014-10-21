package ro.redeul.google.go.completion;

import java.io.IOException;

public class GoCompletionFunctionsTestCase extends GoCompletionTestCase{
    protected String getTestDataRelativePath() {
        return super.getTestDataRelativePath() + "functions";
    }

    public void testLocalFunctionVariants() throws IOException {
        doTestVariants();
    }

    public void testFunctionParameterType() throws IOException {
        doTest();
    }

    public void testInterfaceFunctionParameterType() throws IOException {
        doTest();
    }
}
