package ro.redeul.google.go.completion;

import java.io.IOException;

public class GoBasicCompletionTestCase extends GoCompletionTestCase {
    protected String getTestDataRelativePath() {
        return super.getTestDataRelativePath() + "basic";
    }

    public void testTypeInterfaceCompletion() throws IOException {
        _testSingleCompletion();
    }

    public void testTypeStructCompletion() throws IOException {
        _testSingleCompletion();
    }

    public void testTopLevelConstDeclaration() throws IOException {
        _testSingleCompletion();
    }

    public void testTopLevelVarDeclaration() throws IOException {
        _testSingleCompletion();
    }

    public void testPackage() throws IOException {
        _testSingleCompletion();
    }

    public void testPackageInside() throws IOException {
        _testSingleCompletion();
    }

    public void testImportDeclaration() throws IOException {
        _testSingleCompletion();
    }

    public void testImportedPackages() throws IOException {
        addPackage("fmt", "fmt.go");
        _testVariants();
    }

    public void testReturnCompletion() throws IOException {
        _testSingleCompletion();
    }

    public void testReturnCompletionWithOneResult() throws IOException {
        _testSingleCompletion();
    }
}
