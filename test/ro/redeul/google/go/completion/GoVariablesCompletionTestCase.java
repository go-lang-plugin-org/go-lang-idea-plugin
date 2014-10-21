package ro.redeul.google.go.completion;

import java.io.IOException;

public class GoVariablesCompletionTestCase extends GoCompletionTestCase {
    protected String getTestDataRelativePath() {
        return super.getTestDataRelativePath() + "variables";
    }

    public void testLocalVar() throws IOException {
        doTest();
    }

    public void testEnclosingScopeVar() throws IOException {
        doTest();
    }

    public void testLocalVarVariants() throws IOException {
        doTestVariants();
    }

    public void testLocalVarVariantsCaseInsensitive() throws IOException {
        doTestVariants();
    }

    public void testStructFieldViaChannelExpression() throws IOException {
        doTestVariants();
    }

    public void testMethodReference() throws IOException {
        doTestVariants();
    }

    public void testShortVar() throws IOException {
        doTestVariants();
    }

    public void testVarWithoutType() throws IOException {
        doTestVariants();
    }

    public void testFromDotImportedPackages() throws IOException {
        doTestVariants();
    }
}
