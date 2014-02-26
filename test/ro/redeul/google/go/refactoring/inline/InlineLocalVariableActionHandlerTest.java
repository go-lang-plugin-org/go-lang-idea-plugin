package ro.redeul.google.go.refactoring.inline;

import ro.redeul.google.go.refactoring.GoRefactoringTestCase;

public class InlineLocalVariableActionHandlerTest extends GoRefactoringTestCase {
    public void testSimple() throws Exception { doTest(); }
    public void testParentheses() throws Exception { doTest(); }
    public void testIf() throws Exception { doTest(); }
    public void testVar() throws Exception { doTest(); }
    public void testVarCall() throws Exception { doTest(); }
    public void testShortVar1() throws Exception { doTest(); }
    public void testShortVar2() throws Exception { doTest(); }
    public void testPrecedence1() throws Exception { doTest(); }
    public void testPrecedence2() throws Exception { doTest(); }
    public void testPrecedence3() throws Exception { doTest(); }
    public void testSwitchExpression() throws Exception { doTest(); }
    public void testSwitchType() throws Exception { doTest(); }
}
