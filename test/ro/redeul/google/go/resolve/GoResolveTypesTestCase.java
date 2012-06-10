package ro.redeul.google.go.resolve;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: Sep 8, 2010
 * Time: 2:59:17 PM
 */
public class GoResolveTypesTestCase extends GoResolveTestCase {

    @Override
    protected String getRelativeDataPath() {
        return "types/";
    }

    public void testLocalType() throws Exception {
        doTestResolve();
    }

    public void testFromMethodReceiver() throws Exception {
        doTestResolve();
    }
}
