package ro.redeul.google.go.resolve;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: Sep 8, 2010
 * Time: 2:59:17 PM
 */
public class GoResolveVarsTestCase extends GoResolveTestCase {

    @Override
    protected String getRelativeDataPath() {
        return "vars/";
    }

    public void testDeclaredInForRange() throws Exception {
        doTestResolve();
    }

    public void testDeclaredInForRange2() throws Exception {
        doTestResolve();
    }

    public void testDeclaredInForRangeAsValue() throws Exception {
        doTestResolve();
    }

    public void testDeclaredInForClause() throws Exception {
        doTestResolve();
    }

    public void testMethodReturn() throws Exception {
        doTestResolve();
    }

    public void testSimpleMethodParameter() throws Exception {
        doTestResolve();
    }

    public void testMethodReturn2() throws Exception {
        doTestResolve();
    }

    public void testResolveMethodReceiver() throws Exception {
        doTestResolve();
    }
}
