package ro.redeul.google.go.intentions.statements;

import ro.redeul.google.go.intentions.GoIntentionTestCase;

public class CheckErrorIntentionTest extends GoIntentionTestCase {
    public void testSimpleExpression() throws Exception {
        doTest();
    }

    public void testLiteralExpression() throws Exception {
        doTest();
    }

    public void testLocalIdentifierExpression() throws Exception {
        doTest();
    }

    public void testMultipleErrorsExpression() throws Exception {
        doTest();
    }
}
