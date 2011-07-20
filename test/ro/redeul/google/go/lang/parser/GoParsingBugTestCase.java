package ro.redeul.google.go.lang.parser;

import java.io.File;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: 7/19/11
 * Time: 5:15 PM
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
