package ro.redeul.google.go.resolve;

public class GoResolveStructFieldsTest extends GoPsiResolveTestCase {
    @Override
    protected String getTestDataRelativePath() {
        return super.getTestDataRelativePath() + "struct/";
    }

    public void testDirectStructField() throws Exception {
        doTest();
    }

    public void testAnonymousDirectStructField() throws Exception {
        doTest();
    }

    public void testPromotedStructField() throws Exception {
        doTest();
    }

    public void testDirectExportedFieldFromImportedPackage() throws Exception {
        doTest();
    }

    public void testDirectPrivateFieldFromImportedPackage() throws Exception {
        doTest();
    }
}
