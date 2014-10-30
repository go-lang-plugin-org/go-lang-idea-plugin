package ro.redeul.google.go.completion;

import java.io.IOException;

public class GoVariablesCompletionTestCase extends GoCompletionTestCase {
    protected String getTestDataRelativePath() {
        return super.getTestDataRelativePath() + "variables";
    }

    public void testLocalVar() throws IOException {
        _testSingleCompletion();
    }

    public void testEnclosingScopeVar() throws IOException {
        _testSingleCompletion();
    }

    public void testLocalVarVariants() throws IOException {
        _testVariants();
    }

    public void testLocalVarVariantsCaseInsensitive() throws IOException {
        _testVariants();
    }

    public void testStructFieldViaChannelExpression() throws IOException {
        _testVariants();
    }

    public void testMethodReference() throws IOException {
        _testVariants();
    }

    public void testShortVar() throws IOException {
        _testVariants();
    }

    public void testVarWithoutType() throws IOException {
        _testVariants();
    }

    public void testFromDotImportedPackages() throws IOException {
        addPackage("test", "test/type1.go", "test/type2.go");
        _testVariants();
    }
}
