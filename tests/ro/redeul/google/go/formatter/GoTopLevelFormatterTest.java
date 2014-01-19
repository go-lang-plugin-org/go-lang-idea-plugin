package ro.redeul.google.go.formatter;

import ro.redeul.google.go.GoFormatterTestCase;

/**
 * Top level file formatter test cases.
 * <br/>
 * <p/>
 * Created on Dec-29-2013 22:27
 *
 * @author <a href="mailto:mtoader@gmail.com">Mihai Toader</a>
 */
public class GoTopLevelFormatterTest extends GoFormatterTestCase {

    @Override
    protected String getRelativeTestDataPath() {
        return super.getRelativeTestDataPath() + "toplevel/";
    }

    public void testEof_noWhiteSpace() throws Exception { _test(); }
    public void testEof_tooMuchWhiteSpace() throws Exception { _test(); }

    public void testFunction_multiline() throws Exception { _test(); }
    public void testFunction_normal() throws Exception { _test(); }
    public void testFunction_withMultilineStatements() throws Exception { _test(); }

    public void testImport_normalizeEmptyLines() throws Exception { _test(); }
    public void testImport_simple() throws Exception { _test(); }

    public void testTypes_simple() throws Exception { _test(); }
    public void testTypes_multiple() throws Exception { _test(); }

    public void testAlternatingComments() throws Exception { _test(); }
    public void testBasic() throws Exception { _test(); }

    public void testBasicWithLineComments() throws Exception { _test(); }

    public void testCommentAtTheStart() throws Exception { _test(); }

    public void testConstDeclarations() throws Exception { _test(); }

    public void testGrouping() throws Exception { _test(); }

    public void testVarDeclarations() throws Exception { _test(); }
}
