package ro.redeul.google.go.refactoring.introduce;

public class IntroduceVariableTest extends AbstractIntroduceTest {
    @Override
    protected String getItemName() {
        return "variable";
    }

    @Override
    protected GoIntroduceHandlerBase createHandler() {
        return new GoIntroduceVariableHandler();
    }

    public void testSimple() throws Exception { doTest(); }
    public void testParenthesis1() throws Exception { doTest(); }
    public void testParenthesis2() throws Exception { doTest(); }
    public void testParenthesis3() throws Exception { doTest(); }
    public void testParenthesis4() throws Exception { doTest(); }
}
