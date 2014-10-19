package ro.redeul.google.go.resolve;

public class GoResolvePackageTest extends GoPsiResolveTestCase {

    @Override
    protected String getTestDataRelativePath() {
        return super.getTestDataRelativePath() + "package/";
    }

    public void testAliasedImport() throws Exception {
        doTest();
    }

    public void testDefaultImport() throws Exception {
        doTest();
    }

    public void testDefaultImportWithDifferentName() throws Exception {
        doTest();
    }
}
