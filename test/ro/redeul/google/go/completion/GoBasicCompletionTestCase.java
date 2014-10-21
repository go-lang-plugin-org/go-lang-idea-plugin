package ro.redeul.google.go.completion;

import java.io.IOException;

public class GoBasicCompletionTestCase extends GoCompletionTestCase {
    protected String getTestDataRelativePath() {
        return super.getTestDataRelativePath() + "basic";
    }

    public void testTypeInterfaceCompletion() throws IOException {
        doTest();
    }

    public void testTypeStructCompletion() throws IOException {
        doTest();
    }

    public void testTopLevelConstDeclaration() throws IOException {
        doTest();
    }

    public void testTopLevelVarDeclaration() throws IOException {
        doTest();
    }

    public void testPackage() throws IOException {
        doTest();
    }

    public void testPackageInside() throws IOException {
        doTest();
    }

    public void testImportDeclaration() throws IOException {
        doTest();
    }

    public void testImportedPackages() throws IOException {
        doTestVariants();
    }

    public void testReturnCompletion() throws IOException {
        doTest();
    }

    public void testReturnCompletionWithOneResult() throws IOException {
        doTest();
    }
}
