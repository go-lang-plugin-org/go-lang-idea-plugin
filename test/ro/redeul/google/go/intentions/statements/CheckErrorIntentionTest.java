package ro.redeul.google.go.intentions.statements;

import org.junit.Ignore;
import ro.redeul.google.go.intentions.GoIntentionTestCase;

public class CheckErrorIntentionTest extends GoIntentionTestCase {

    @Ignore("broken by the new resolver")
    public void testSimpleExpression() throws Exception {
        doTest();
    }

    public void testLiteralExpression() throws Exception {
        doTest();
    }

    public void testLocalIdentifierExpression() throws Exception {
        doTest();
    }

    @Ignore("broken by the new resolver")
    public void testMultipleErrorsExpression() throws Exception {
        doTest();
    }
}
