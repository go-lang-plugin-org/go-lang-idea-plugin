package ro.redeul.google.go.refactoring.introduce;

public class IntroduceConstantTest extends AbstractIntroduceTest {
    @Override
    protected GoIntroduceHandlerBase createHandler() {
        return new GoIntroduceConstantHandler();
    }

    @Override
    protected String getItemName() {
        return "constant";
    }

    public void testSimple() throws Exception { doTest(); }
    public void testOneConst() throws Exception { doTest(); }
    public void testOneConstWithParenthesis() throws Exception { doTest(); }
    public void testAfterImport() throws Exception { doTest(); }
    public void testMultiConsts() throws Exception { doTest(); }
    public void testMultiConstsInOneLine() throws Exception { doTest(); }
}
