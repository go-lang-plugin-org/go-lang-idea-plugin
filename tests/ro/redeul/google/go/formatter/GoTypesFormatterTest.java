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
public class GoTypesFormatterTest extends GoFormatterTestCase {

    @Override
    protected String getRelativeTestDataPath() {
        return super.getRelativeTestDataPath() + "types/";
    }

    public void testStruct_simple() throws Exception { _test(); }
    public void testStruct_empty() throws Exception { _test(); }
    public void testStruct_nested() throws Exception { _test(); }

    public void testInterface_simple() throws Exception { _test(); }
    public void testInterface_empty() throws Exception { _test(); }

    public void testArray() throws Exception { _test(); }
    public void testPointer() throws Exception { _test(); }
    public void testMap() throws Exception { _test(); }
    public void testSlice() throws Exception { _test(); }
    public void testChannel() throws Exception { _test(); }
    public void testFunction() throws Exception { _test(); }
}
