package ro.redeul.google.go.refactoring;

import ro.redeul.google.go.refactoring.introduce.GoIntroduceVariableHandler;

public class IntroduceVariableTest
    extends GoRefactoringTestCase<GoIntroduceVariableHandler> {

    @Override
    protected GoIntroduceVariableHandler createHandler() {
        return new GoIntroduceVariableHandler();
    }

    public void testSimple() throws Exception { doTest(); }
    public void testParenthesis1() throws Exception { doTest(); }
    public void testParenthesis2() throws Exception { doTest(); }
    public void testParenthesis3() throws Exception { doTest(); }
    public void testParenthesis4() throws Exception { doTest(); }
    public void testMultiLine() throws Exception { doTest(); }
}
