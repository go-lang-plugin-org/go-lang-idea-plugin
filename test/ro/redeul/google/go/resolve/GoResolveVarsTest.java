package ro.redeul.google.go.resolve;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: Sep 8, 2010
 * Time: 2:59:17 PM
 */
public class GoResolveVarsTest extends GoPsiResolveTestCase {

    @Override
    protected String getTestDataRelativePath() {
        return super.getTestDataRelativePath() + "vars/";
    }

    public void testDeclaredInForRange() throws Exception {
        doTest();
    }

    public void testDeclaredInForRange2() throws Exception {
        doTest();
    }

    public void testDeclaredInForRangeAsValue() throws Exception {
        doTest();
    }

    public void testDeclaredInForClause() throws Exception {
        doTest();
    }

    public void testMethodReturn() throws Exception {
        doTest();
    }

    public void testSimpleMethodParameter() throws Exception {
        doTest();
    }

    public void testMethodReturn2() throws Exception {
        doTest();
    }

    public void testResolveMethodReceiver() throws Exception {
        doTest();
    }
}
