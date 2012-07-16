package ro.redeul.google.go.intentions.statements;

import ro.redeul.google.go.intentions.GoIntentionTestCase;

public class MoveSimpleStatementOutIntentionTest extends GoIntentionTestCase {
    public void testSimpleIf() throws Exception { doTest(); }
    public void testIfChain() throws Exception { doTest(); }

    public void testSwitchExpression1()  throws Exception { doTest(); }
    public void testSwitchExpression2()  throws Exception { doTest(); }

    public void testSwitchType()  throws Exception { doTest(); }

    public void testFor()  throws Exception { doTest(); }
}
