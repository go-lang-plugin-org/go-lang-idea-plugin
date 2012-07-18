package ro.redeul.google.go.intentions.control;

import ro.redeul.google.go.intentions.GoIntentionTestCase;

public class MergeIfAndIntentionTest extends GoIntentionTestCase {
    public void testSimple() throws Exception { doTest(); }
    public void testOnlyInnerHasSimpleStatement() throws Exception { doTest(); }
    public void testOnlyOuterHasSimpleStatement() throws Exception { doTest(); }
    public void testBothHaveSimpleStatements() throws Exception { doTest(); }
    public void testInnerHasElse() throws Exception { doTest(); }
    public void testInnerIsOrExpression() throws Exception { doTest(); }
}
