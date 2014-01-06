package ro.redeul.google.go.parsing;

import ro.redeul.google.go.GoParsingTestCase;

/**
 * TODO: Document this
 * <p/>
 * Created on Jan-02-2014 15:51
 *
 * @author <a href="mailto:mtoader@gmail.com">Mihai Toader</a>
 */
public class GoBugsParsingTestCase extends GoParsingTestCase {

    @Override
    protected String getRelativeTestDataPath() {
        return super.getRelativeTestDataPath() + "bugs";
    }

    public void testSlice() throws Exception { _test(); }

    public void testCollate() throws Exception { _test(); }

//    public void testTables() throws Exception { _test(); }
}
