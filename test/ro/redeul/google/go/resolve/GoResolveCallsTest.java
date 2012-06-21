package ro.redeul.google.go.resolve;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: Sep 8, 2010
 * Time: 2:59:17 PM
 */
public class GoResolveCallsTest extends GoPsiResolveTestCase {

    @Override
    protected String getTestDataRelativePath() {
        return super.getTestDataRelativePath() + "calls/";
    }

    public void testCallToLocalMethod() throws Exception {
        doTest();
    }

    public void testCallToLocalFunction() throws Exception {
        doTest();
    }

    public void testTypeConversionToLocalType() throws Exception {
        doTest();
    }

    public void testConversionToImportedType() throws Exception {
        doTest();
    }

    public void testConversionToLocallyImportedType() throws Exception {
        doTest();
    }

    public void testNoConversionToBlankImportedType() throws Exception {
        doTest();
    }

    public void testConversionToImportedFunction() throws Exception {
        doTest();
    }

//    public void testConversionToLocallyImportedType() throws Exception {
//        doTest();
//    }
//
//    public void testNoConversionToBlankImportedType() throws Exception {
//        doTest();
//    }
}
