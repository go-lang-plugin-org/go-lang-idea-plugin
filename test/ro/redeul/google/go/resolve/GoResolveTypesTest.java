package ro.redeul.google.go.resolve;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: Sep 8, 2010
 * Time: 2:59:17 PM
 */
public class GoResolveTypesTest extends GoPsiResolveTestCase {

    @Override
    protected String getTestDataRelativePath() {
        return super.getTestDataRelativePath() + "types/";
    }

    public void testLocalType() throws Exception {
        doTest();
    }

    public void testFromMethodReceiver() throws Exception {
        doTest();
    }

    public void testFromDefaultImportedPackage() throws Exception {
        doTest();
    }

    public void testFromInjectedPackage() throws Exception {
        doTest();
    }

    public void testFromCustomImportedPackage() throws Exception {
        doTest();
    }

    public void testIgnoreBlankImportedPackage() throws Exception {
        doTest();
    }

    public void testFromNestedImportedPackage() throws Exception {
        doTest();
    }
}
