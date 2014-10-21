package ro.redeul.google.go.completion;

import java.io.IOException;

public class GoMethodsCompletionTestCase extends GoCompletionTestCase{
    protected String getTestDataRelativePath() {
        return super.getTestDataRelativePath() + "methods";
    }

    public void testLocalInheritedMethodsVariants() throws IOException {
        doTestVariants();
    }
}
