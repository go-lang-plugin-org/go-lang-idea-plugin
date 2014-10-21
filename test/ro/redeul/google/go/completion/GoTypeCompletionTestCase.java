package ro.redeul.google.go.completion;

import java.io.IOException;

public class GoTypeCompletionTestCase extends GoCompletionTestCase {
    protected String getTestDataRelativePath() {
        return super.getTestDataRelativePath() + "types";
    }

    public void testListTypesOnly() throws IOException {
        doTestVariants();
    }

    public void testAlsoListImportedPackages() throws IOException {
        doTestVariants();
    }

    public void testMethodReceiver() throws IOException {
        doTestVariants();
    }

    public void testFromImportedPackages() throws IOException {
        doTestVariants();
    }
    public void testFromDotImportedPackages() throws IOException {
        doTestVariants();
    }
}
