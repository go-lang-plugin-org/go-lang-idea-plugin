package ro.redeul.google.go.refactoring.introduce;

import ro.redeul.google.go.refactoring.GoRefactoringTestCase;

public class GoIntroduceVariableHandlerTest extends GoRefactoringTestCase {
    public void testSimple() throws Exception { doTest(); }
    public void testMultiOccurrences() throws Exception { doTest(); }
    public void testIgnoreGlobalAndParameter() throws Exception { doTest(); }
    public void testRespectIdentifierScope() throws Exception { doTest(); }
    public void testParenthesis1() throws Exception { doTest(); }
    public void testParenthesis2() throws Exception { doTest(); }
    public void testParenthesis3() throws Exception { doTest(); }
    public void testParenthesis4() throws Exception { doTest(); }
    public void testNamedResultFunctionCall() throws Exception { doTest(); }
    public void testUnnamedResultFunctionCall() throws Exception { doTest(); }
    public void testMultiLine() throws Exception { doTest(); }
}
