package ro.redeul.google.go.parsing;

import ro.redeul.google.go.GoParsingTestCase;

import java.io.File;

/**
 * TODO: Document this
 * <p/>
 * Created on Jan-12-2014 19:11
 *
 * @author <a href="mailto:mtoader@gmail.com">Mihai Toader</a>
 */
public class GoCommentsParsingTestCase extends GoParsingTestCase {

    @Override
    protected String getRelativeTestDataPath() {
        return super.getRelativeTestDataPath() + File.separator + "comments";
    }

    public void testSingle_simple() throws Exception { _test(); }
    public void testSingle_embedded() throws Exception { _test(); }

    public void testMulti_simple() throws Exception { _test(); }
    public void testMulti_embedded() throws Exception { _test(); }
    public void testMulti_multiline() throws Exception { _test(); }
}
