package ro.redeul.google.go.lang.parser;

import java.io.File;

public class GoStatementsTestCase extends GoParsingTestCase {

    @Override
    protected String getTestDataRelativePath() {
        return super.getTestDataRelativePath() + File.separator + "statements";
    }

    public void testContinue_empty() throws Throwable { doTest(); }
    public void testContinue_empty2() throws Throwable { doTest(); }
    public void testContinue_simple() throws Throwable { doTest(); }
    public void testContinue_commentsAfterEnd() throws Throwable { doTest(); }


    public void testIf_case1() throws Throwable { doTest(); }
    public void testIf_case2() throws Throwable { doTest(); }
    public void testIf_case3() throws Throwable { doTest(); }
    public void testIf_case4() throws Throwable { doTest(); }
    public void testIf_case5() throws Throwable { doTest(); }
    public void testIf_case6() throws Throwable { doTest(); }
    public void testIf_case7() throws Throwable { doTest(); }
    public void testIf_case8() throws Throwable { doTest(); }
    public void testIf_case9() throws Throwable { doTest(); }

    public void testIf_else1() throws Throwable { doTest(); }
    public void testIf_else2() throws Throwable { doTest(); }
    public void testIf_elseIf1() throws Throwable { doTest(); }

    public void testIf_commentsAfter() throws Throwable { doTest(); }
    public void testIf_incomplete() throws Throwable { doTest(); }

    public void testLabeled_case1() throws Throwable { doTest(); }
    public void testLabeled_case2() throws Throwable { doTest(); }
    public void testLabeled_case3() throws Throwable { doTest(); }

    public void testGoto_goto1() throws Throwable { doTest(); }

    public void testFor_case1() throws Throwable { doTest(); }
    public void testFor_case2() throws Throwable { doTest(); }
    public void testFor_case3() throws Throwable { doTest(); }
    public void testFor_case4() throws Throwable { doTest(); }
    public void testFor_case5() throws Throwable { doTest(); }
    public void testFor_case6() throws Throwable { doTest(); }
    public void testFor_case7() throws Throwable { doTest(); }
    public void testFor_case8() throws Throwable { doTest(); }
    public void testFor_case9() throws Throwable { doTest(); }
    public void testFor_case10() throws Throwable { doTest(); }
    public void testFor_case11() throws Throwable { doTest(); }
    public void testFor_case12() throws Throwable { doTest(); }
    public void testFor_case13() throws Throwable { doTest(); }
    public void testFor_case14() throws Throwable { doTest(); }
    public void testFor_case15() throws Throwable { doTest(); }
    public void testFor_commentsAfterEnd() throws Throwable { doTest(); }
    public void testFor_rangeLiteral() throws Throwable { doTest(); }
    public void testFor_incomplete() throws Throwable { doTest(); }

    public void testAssignment_simple() throws Throwable { doTest(); }
    public void testAssignment_double() throws Throwable { doTest(); }
    public void testAssignment_opAssign() throws Throwable { doTest(); }
    public void testAssignment_functionWithMultipleResults() throws Throwable { doTest(); }

    public void testGo_anonymousFunction() throws Throwable { doTest(); }

}
