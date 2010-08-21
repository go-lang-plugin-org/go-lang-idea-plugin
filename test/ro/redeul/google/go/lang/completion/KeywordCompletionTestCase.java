package ro.redeul.google.go.lang.completion;

import org.junit.Test;

/**
 * Created by IntelliJ IDEA.
 * User: mtoader
 * Date: Aug 20, 2010
 * Time: 5:29:50 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class KeywordCompletionTestCase extends GoCompletionTestBase {

    @Override
    protected String getBasePath() {
        return super.getBasePath() + "keywords/";
    }

    @Test
    public void testP() throws Throwable { doBasicTest(); }
}
