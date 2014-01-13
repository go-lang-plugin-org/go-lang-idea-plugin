package ro.redeul.google.go.formatter;

import ro.redeul.google.go.GoFormatterTestCase;

/**
 * TODO: Document this
 * <p/>
 * Created on Jan-12-2014 18:49
 *
 * @author <a href="mailto:mtoader@gmail.com">Mihai Toader</a>
 */
public class GoSanityFormatterTest extends GoFormatterTestCase {
    @Override
    protected String getRelativeTestDataPath() {
        return super.getRelativeTestDataPath() + "sanity/";
    }

    public void testAssignment() throws Exception { _test(); }

    public void testBinaryExpressions() throws Exception { _test(); }

    public void testCallParameters() throws Exception { _test(); }

    public void testCastFunc() throws Exception { _test(); }

    public void testComposites() throws Exception { _test(); }

    public void testCrlf() throws Exception { _test(); }

    public void testFunctionCall() throws Exception { _test(); }

    public void testFunctionDeclaration() throws Exception { _test(); }

    public void testImport() throws Exception { _test(); }

    public void testInterfaceType() throws Exception { _test(); }

    public void testSlices1() throws Exception { _test(); }

    public void testSlices2() throws Exception { _test(); }

    public void testTopLevel() throws Exception { _test(); }

    public void testType() throws Exception { _test(); }

    public void testTypeswitch() throws Exception { _test(); }
}
