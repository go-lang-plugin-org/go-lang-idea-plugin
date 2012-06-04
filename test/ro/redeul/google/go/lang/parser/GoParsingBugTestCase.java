package ro.redeul.google.go.lang.parser;

import java.io.File;

/**
 * @author Mihai Claudiu Toader <mtoader@gmail.com>
 * Date: 7/19/11
 */
public class GoParsingBugTestCase extends GoParsingTestCase {

    @Override
    protected String getBasePath() {
        return super.getBasePath() + File.separator + "bugs";
    }

    public void testComment$first() throws Throwable {
        doTest();
    }

    public void testComment$second() throws Throwable {
        doTest();
    }

    public void testComment$elf() throws Throwable {
        doTest();
    }

}
