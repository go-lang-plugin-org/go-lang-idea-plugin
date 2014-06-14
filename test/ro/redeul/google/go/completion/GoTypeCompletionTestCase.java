package ro.redeul.google.go.completion;

public class GoTypeCompletionTestCase extends GoCompletionTestCase {
    protected String getTestDataRelativePath() {
        return super.getTestDataRelativePath() + "types";
    }

    public void testListTypesOnly() {
        doTestVariants();
    }

    public void testAlsoListImportedPackages() {
        doTestVariants();
    }

    public void testMethodReceiver() {
        doTestVariants();
    }

    public void testFromImportedPackages() {
        doTestVariants();
    }
    public void testFromDotImportedPackages() {
        doTestVariants();
    }
}
