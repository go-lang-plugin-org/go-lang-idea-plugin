package ro.redeul.google.go.completion;

public class GoBasicCompletionTestCase extends GoCompletionTestCase {
    protected String getTestDataRelativePath() {
        return super.getTestDataRelativePath() + "basic";
    }

    public void testTypeInterfaceCompletion() {
        doTest();
    }

    public void testTypeStructCompletion() {
        doTest();
    }

    public void testTopLevelConstDeclaration() {
        doTest();
    }

    public void testTopLevelVarDeclaration() {
        doTest();
    }

    public void testPackage() {
        doTest();
    }

    public void testPackageInside() {
        doTest();
    }

    public void testImportDeclaration() {
        doTest();
    }
}
