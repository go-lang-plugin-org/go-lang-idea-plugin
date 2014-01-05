package ro.redeul.google.go.lang.parser;

import java.io.File;

public class GoStatementsTestCase extends GoParsingTestCase {

    @Override
    protected String getTestDataRelativePath() {
        return super.getTestDataRelativePath() + File.separator + "statements";
    }

    public void testReturn_empty() throws Throwable { doTest(); }
    public void testReturn_emptySemi() throws Throwable { doTest(); }
    public void testReturn_emptySemi2() throws Throwable { doTest(); }
    public void testReturn_emptyEnter() throws Throwable { doTest(); }
    public void testReturn_emptyEnter2() throws Throwable { doTest(); }
    public void testReturn_multiple() throws Throwable { doTest(); }
    public void testReturn_multiple2() throws Throwable { doTest(); }
    public void testReturn_multiple3() throws Throwable { doTest(); }
    public void testReturn_commentsAfterEnd() throws Throwable { doTest(); }
    public void testReturn_expressionAfter() throws Throwable { doTest(); }

    public void testVar_var1() throws Throwable { doTest(); }
    public void testVar_var2() throws Throwable { doTest(); }
    public void testVar_var3() throws Throwable { doTest(); }
    public void testVar_var4() throws Throwable { doTest(); }

    public void testBreak_empty() throws Throwable { doTest(); }
    public void testBreak_empty2() throws Throwable { doTest(); }
    public void testBreak_simple() throws Throwable { doTest(); }
    public void testBreak_commentsAfterEnd() throws Throwable { doTest(); }

    public void testContinue_empty() throws Throwable { doTest(); }
    public void testContinue_empty2() throws Throwable { doTest(); }
    public void testContinue_simple() throws Throwable { doTest(); }
    public void testContinue_commentsAfterEnd() throws Throwable { doTest(); }

    public void testSwitch_expr_case1() throws Throwable { doTest(); }
    public void testSwitch_expr_case2() throws Throwable { doTest(); }
    public void testSwitch_expr_case3() throws Throwable { doTest(); }
    public void testSwitch_expr_case4() throws Throwable { doTest(); }
    public void testSwitch_expr_case5() throws Throwable { doTest(); }
    public void testSwitch_expr_case6() throws Throwable { doTest(); }
    public void testSwitch_expr_case7() throws Throwable { doTest(); }
    public void testSwitch_expr_case8() throws Throwable { doTest(); }
    public void testSwitch_expr_case9() throws Throwable { doTest(); }
    public void testSwitch_expr_case10() throws Throwable { doTest(); }
    public void testSwitch_expr_case11() throws Throwable { doTest(); }
    public void testSwitch_expr_commentsAfterEnd() throws Throwable { doTest(); }
    public void testSwitch_expr_withBreak() throws Throwable { doTest(); }

    public void testSwitch_type_case1() throws Throwable { doTest(); }
    public void testSwitch_type_case2() throws Throwable { doTest(); }
    public void testSwitch_type_case3() throws Throwable { doTest(); }
    public void testSwitch_type_case4() throws Throwable { doTest(); }
    public void testSwitch_type_case5() throws Throwable { doTest(); }
    public void testSwitch_type_case6() throws Throwable { doTest(); }
    public void testSwitch_type_case7() throws Throwable { doTest(); }

    public void testSwitch_type_commentsAfterEnd() throws Throwable { doTest(); }

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
