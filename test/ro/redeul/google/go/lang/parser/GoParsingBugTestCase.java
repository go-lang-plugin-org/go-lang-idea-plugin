package ro.redeul.google.go.lang.parser;

import java.io.File;

import org.junit.Ignore;

/**
 * @author Mihai Claudiu Toader <mtoader@gmail.com>
 * Date: 7/19/11
 */
public class GoParsingBugTestCase extends GoParsingTestCase {

    @Override
    protected String getTestDataRelativePath() {
        return super.getTestDataRelativePath() + File.separator + "bugs";
    }

    public void testComment_first() throws Throwable {
        doTest();
    }

    public void testComment_second() throws Throwable {
        doTest();
    }

    public void testComment_elf() throws Throwable {
        doTest();
    }

    @Ignore
    public void testLoops_switchWithExpr() throws Throwable {
        doTest();
    }

    public void testSwitch_stmt_with_compositeLiteral() throws Throwable { doTest(); }
    public void testSwitch_stmt_with_parenthesized() throws Throwable { doTest(); }
    public void testfunction_with_string() throws Throwable { doTest(); }

}
