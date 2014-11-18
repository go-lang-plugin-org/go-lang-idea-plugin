package ro.redeul.google.go.completion;

import org.junit.Ignore;

import java.io.IOException;

public class GoCompletionBugsTestCase extends GoCompletionTestCase{
    protected String getTestDataRelativePath() {
        return super.getTestDataRelativePath() + "bugs";
    }

    public void testGH218_MissingTypeSpec() throws IOException {
        _testVariants();
    }

    public void testGH530_MissingFunctionName() throws IOException {
        _testVariants();
    }

    public void testGH749() throws IOException {
        addPackage("net", "net.go");
        _testSingleCompletion();
    }

    public void testMissingMethodInAnotherPackage() throws IOException {
        addPackage("net", "net.go");
        _testVariants();
    }
}
