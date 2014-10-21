package ro.redeul.google.go.completion;

import java.io.IOException;

public class GoCompletionBugsTestCase extends GoCompletionTestCase{
    protected String getTestDataRelativePath() {
        return super.getTestDataRelativePath() + "bugs";
    }

    public void testGH218_MissingTypeSpec() throws IOException {
        doTestVariants();
    }

    public void testGH530_MissingFunctionName() throws IOException {
        doTestVariants();
    }

    public void testGH749() {doTestVariants("net.go");}
}
