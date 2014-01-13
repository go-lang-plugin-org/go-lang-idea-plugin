package ro.redeul.google.go.formatter;

import ro.redeul.google.go.GoFormatterTestCase;

/**
 * TODO: Document this
 * <p/>
 * Created on Jan-10-2014 21:48
 *
 * @author <a href="mailto:mtoader@gmail.com">Mihai Toader</a>
 */
public class GoExpressionsFormatterTest extends GoFormatterTestCase {

    @Override
    protected String getRelativeTestDataPath() {
        return super.getRelativeTestDataPath() + "expressions/";
    }


    public void testCalls() throws Exception { _test(); }
    public void testCombined() throws Exception { _test(); }
    public void testIndex() throws Exception { _test(); }
    public void testNormal() throws Exception { _test(); }
    public void testParenthesized() throws Exception { _test(); }
    public void testSlice() throws Exception { _test(); }

    public void testTypeAssertion() throws Exception { _test(); }
}
