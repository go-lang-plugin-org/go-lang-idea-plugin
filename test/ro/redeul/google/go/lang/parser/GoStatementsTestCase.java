package ro.redeul.google.go.lang.parser;

import java.io.File;

public class GoStatementsTestCase extends GoParsingTestCase {

    @Override
    protected String getBasePath() {
        return super.getBasePath() + File.separator + "statements";
    }

    public void testReturn$empty() throws Throwable { doTest(); }

    public void testReturn$empty_semi() throws Throwable { doTest(); }

    public void testReturn$empty_enter() throws Throwable { doTest(); }

    public void testVar$var1() throws Throwable { doTest(); }
    public void testVar$var2() throws Throwable { doTest(); }
    public void testVar$var3() throws Throwable { doTest(); }
    public void testVar$var4() throws Throwable { doTest(); }

    public void testBreak$empty() throws Throwable { doTest(); }
    public void testBreak$empty2() throws Throwable { doTest(); }
    public void testBreak$simple() throws Throwable { doTest(); }

    public void testContinue$empty() throws Throwable { doTest(); }
    public void testContinue$empty2() throws Throwable { doTest(); }
    public void testContinue$simple() throws Throwable { doTest(); }

    public void testSwitch$expr$case1() throws Throwable { doTest(); }
    public void testSwitch$expr$case2() throws Throwable { doTest(); }
    public void testSwitch$expr$case3() throws Throwable { doTest(); }
    public void testSwitch$expr$case4() throws Throwable { doTest(); }
    public void testSwitch$expr$case5() throws Throwable { doTest(); }
    public void testSwitch$expr$case6() throws Throwable { doTest(); }
    public void testSwitch$expr$case7() throws Throwable { doTest(); }
    public void testSwitch$expr$case8() throws Throwable { doTest(); }
    public void testSwitch$expr$case9() throws Throwable { doTest(); }
    public void testSwitch$expr$case10() throws Throwable { doTest(); }
    public void testSwitch$expr$case11() throws Throwable { doTest(); }

    public void testSwitch$type$case1() throws Throwable { doTest(); }
    public void testSwitch$type$case2() throws Throwable { doTest(); }
    public void testSwitch$type$case3() throws Throwable { doTest(); }
    public void testSwitch$type$case4() throws Throwable { doTest(); }
    public void testSwitch$type$case5() throws Throwable { doTest(); }
    public void testSwitch$type$case6() throws Throwable { doTest(); }
    public void testSwitch$type$case7() throws Throwable { doTest(); }

    public void testIf$case1() throws Throwable { doTest(); }
    public void testIf$case2() throws Throwable { doTest(); }
    public void testIf$case3() throws Throwable { doTest(); }
    public void testIf$case4() throws Throwable { doTest(); }
    public void testIf$case5() throws Throwable { doTest(); }
    public void testIf$case6() throws Throwable { doTest(); }
    public void testIf$case7() throws Throwable { doTest(); }
    public void testIf$case8() throws Throwable { doTest(); }
    public void testIf$case9() throws Throwable { doTest(); }

    public void testIf$else1() throws Throwable { doTest(); }
    public void testIf$else2() throws Throwable { doTest(); }
    public void testIf$else_if1() throws Throwable { doTest(); }

    public void testLabeled$case1() throws Throwable { doTest(); }
    public void testLabeled$case2() throws Throwable { doTest(); }
    public void testLabeled$case3() throws Throwable { doTest(); }

    public void testSelect$empty() throws Throwable { doTest(); }
    public void testSelect$case1() throws Throwable { doTest(); }
    public void testSelect$case2() throws Throwable { doTest(); }
    public void testSelect$case3() throws Throwable { doTest(); }
    public void testSelect$case4() throws Throwable { doTest(); }
//    @Test public void testSelectCase2() throws Throwable { doTest(); }
//    @Test public void testSelectCase3() throws Throwable { doTest(); }


    public void testFor$case1() throws Throwable { doTest(); }
    public void testFor$case2() throws Throwable { doTest(); }
    public void testFor$case3() throws Throwable { doTest(); }
    public void testFor$case4() throws Throwable { doTest(); }
    public void testFor$case5() throws Throwable { doTest(); }
    public void testFor$case6() throws Throwable { doTest(); }
    public void testFor$case7() throws Throwable { doTest(); }
    public void testFor$case8() throws Throwable { doTest(); }
    public void testFor$case9() throws Throwable { doTest(); }
    public void testFor$case10() throws Throwable { doTest(); }
    public void testFor$case11() throws Throwable { doTest(); }
    public void testFor$case12() throws Throwable { doTest(); }
    public void testFor$case13() throws Throwable { doTest(); }
    public void testFor$case14() throws Throwable { doTest(); }
    public void testFor$case15() throws Throwable { doTest(); }

}
