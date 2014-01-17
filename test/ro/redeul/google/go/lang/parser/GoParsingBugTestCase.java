package ro.redeul.google.go.lang.parser;

import java.io.File;

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

    public void testLoops_switchWithCompositeLiteral() throws Throwable { doTest(); }
    public void testLoops_assignWithParenthesized() throws Throwable { doTest(); }
    public void testLoops_functionWithString() throws Throwable { doTest(); }
    public void testLoops_nonParsableStatement() throws Throwable { doTest(); }
    public void testStatements_lineBreakAfterLiteral() throws Throwable { doTest(); }

    public void testImports_packageWithDigits() throws Throwable { doTest(); }

    public void testLiterals_wrongRune() throws Throwable { doTest(); }
}
