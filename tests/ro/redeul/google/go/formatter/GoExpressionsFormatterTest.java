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


    public void testBinary_combined() throws Exception { _test(); }
    public void testBinary_multiline() throws Exception { _test(); }
    public void testBinary_normal() throws Exception { _test(); }

    public void testCall_binary() throws Exception { _test(); }
    public void testCall_multiline() throws Exception { _test(); }

    public void testIndex() throws Exception { _test(); }
    public void testInsideAssignments() throws Exception { _test(); }

    public void testParenthesized() throws Exception { _test(); }

    public void testSelector() throws Exception { _test(); }
    public void testSlice() throws Exception { _test(); }

    public void testTypeAssertion() throws Exception { _test(); }
}
