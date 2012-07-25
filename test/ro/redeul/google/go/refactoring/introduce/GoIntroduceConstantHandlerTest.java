package ro.redeul.google.go.refactoring.introduce;

import ro.redeul.google.go.refactoring.GoRefactoringTestCase;

public class GoIntroduceConstantHandlerTest extends GoRefactoringTestCase {
    public void testSimple() throws Exception { doTest(); }
    public void testOneConst() throws Exception { doTest(); }
    public void testInConst() throws Exception { doTest(); }
    public void testOneConstAtBottom() throws Exception { doTest(); }
    public void testOneConstWithParenthesis() throws Exception { doTest(); }
    public void testWholeFileReplace() throws Exception { doTest(); }
    public void testAfterImport() throws Exception { doTest(); }
    public void testMultiConsts() throws Exception { doTest(); }
    public void testMultiConstsInOneLine() throws Exception { doTest(); }
}
