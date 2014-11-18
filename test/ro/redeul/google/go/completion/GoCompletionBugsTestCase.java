package ro.redeul.google.go.completion;

import java.io.IOException;

public class GoCompletionBugsTestCase extends GoCompletionTestCase{
    protected String getTestDataRelativePath() {
        return super.getTestDataRelativePath() + "bugs";
    }

    public void testMissingTypeSpec() throws IOException {
        _testVariants();
    }

    public void testMissingFunctionName() throws IOException {
        _testVariants();
    }

    public void testLocalPackageCompletion() throws IOException {
        addPackage("net", "net.go");
        _testSingleCompletion();
    }

    public void testMissingMethodInAnotherPackage() throws IOException {
        addPackage("net", "net.go");
        _testVariants();
    }
}
