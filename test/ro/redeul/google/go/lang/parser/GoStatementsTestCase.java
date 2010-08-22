package ro.redeul.google.go.lang.parser;

import org.testng.annotations.Test;

import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: mtoader
 * Date: Aug 5, 2010
 * Time: 9:56:52 PM
 * To change this template use File | Settings | File Templates.
 */
public class GoStatementsTestCase extends GoParsingTestCase {

    @Override
    protected String getRelativeDataPath() {
        return super.getRelativeDataPath() + File.separator + "statements";
    }

    @Test public void testReturnEmpty() throws Throwable { doTest(); }

    @Test public void testReturnEmpty_semi() throws Throwable { doTest(); }

    @Test public void testReturnEmpty_enter() throws Throwable { doTest(); }

    @Test public void testVarVar1() throws Throwable { doTest(); }
    
    @Test public void testVarVar2() throws Throwable { doTest(); }

    @Test public void testVarVar3() throws Throwable { doTest(); }

    @Test public void testBreakEmpty() throws Throwable { doTest(); }
    @Test public void testBreakEmpty2() throws Throwable { doTest(); }
    @Test public void testBreakSimple() throws Throwable { doTest(); }

    @Test public void testContinueEmpty() throws Throwable { doTest(); }
    @Test public void testContinueEmpty2() throws Throwable { doTest(); }
    @Test public void testContinueSimple() throws Throwable { doTest(); }

    @Test public void testSwitchExprCase1() throws Throwable { doTest(); }
    @Test public void testSwitchExprCase2() throws Throwable { doTest(); }
    @Test public void testSwitchExprCase3() throws Throwable { doTest(); }
    @Test public void testSwitchExprCase4() throws Throwable { doTest(); }
    @Test public void testSwitchExprCase5() throws Throwable { doTest(); }
    @Test public void testSwitchExprCase6() throws Throwable { doTest(); }
    @Test public void testSwitchExprCase7() throws Throwable { doTest(); }
    @Test public void testSwitchExprCase8() throws Throwable { doTest(); }
    @Test public void testSwitchExprCase9() throws Throwable { doTest(); }
    @Test public void testSwitchExprCase10() throws Throwable { doTest(); }
    @Test public void testSwitchExprCase11() throws Throwable { doTest(); }

    @Test public void testSwitchTypeCase1() throws Throwable { doTest(); }
    @Test public void testSwitchTypeCase2() throws Throwable { doTest(); }
    @Test public void testSwitchTypeCase3() throws Throwable { doTest(); }
    @Test public void testSwitchTypeCase4() throws Throwable { doTest(); }
    @Test public void testSwitchTypeCase5() throws Throwable { doTest(); }
    @Test public void testSwitchTypeCase6() throws Throwable { doTest(); }
    @Test public void testSwitchTypeCase7() throws Throwable { doTest(); }

    @Test public void testIfCase1() throws Throwable { doTest(); }
    @Test public void testIfCase2() throws Throwable { doTest(); }
    @Test public void testIfCase3() throws Throwable { doTest(); }
    @Test public void testIfCase4() throws Throwable { doTest(); }
    @Test public void testIfCase5() throws Throwable { doTest(); }
    @Test public void testIfCase6() throws Throwable { doTest(); }
    @Test public void testIfCase7() throws Throwable { doTest(); }
    @Test public void testIfCase8() throws Throwable { doTest(); }
    @Test public void testIfCase9() throws Throwable { doTest(); }

    @Test public void testIfElse1() throws Throwable { doTest(); }
    @Test public void testIfElse2() throws Throwable { doTest(); }
    @Test public void testIfElseif1() throws Throwable { doTest(); }
    
    @Test public void testLabeledCase1() throws Throwable { doTest(); }
    @Test public void testLabeledCase2() throws Throwable { doTest(); }
    @Test public void testLabeledCase3() throws Throwable { doTest(); }

    @Test public void testSelectEmpty() throws Throwable { doTest(); }
    @Test public void testSelectCase1() throws Throwable { doTest(); }
    @Test public void testSelectCase2() throws Throwable { doTest(); }
    @Test public void testSelectCase3() throws Throwable { doTest(); }
    @Test public void testSelectCase4() throws Throwable { doTest(); }
//    @Test public void testSelectCase2() throws Throwable { doTest(); }
//    @Test public void testSelectCase3() throws Throwable { doTest(); }
    
    
    @Test public void testForCase1() throws Throwable { doTest(); }
    @Test public void testForCase2() throws Throwable { doTest(); }
    @Test public void testForCase3() throws Throwable { doTest(); }
    @Test public void testForCase4() throws Throwable { doTest(); }
    @Test public void testForCase5() throws Throwable { doTest(); }
    @Test public void testForCase6() throws Throwable { doTest(); }
    @Test public void testForCase7() throws Throwable { doTest(); }
    @Test public void testForCase8() throws Throwable { doTest(); }
    @Test public void testForCase9() throws Throwable { doTest(); }
    @Test public void testForCase10() throws Throwable { doTest(); }
    @Test public void testForCase11() throws Throwable { doTest(); }
    @Test public void testForCase12() throws Throwable { doTest(); }
    @Test public void testForCase13() throws Throwable { doTest(); }

}
