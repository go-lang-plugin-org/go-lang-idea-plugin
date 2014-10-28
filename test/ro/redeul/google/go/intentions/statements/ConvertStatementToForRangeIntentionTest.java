package ro.redeul.google.go.intentions.statements;

import org.junit.Ignore;
import ro.redeul.google.go.intentions.GoIntentionTestCase;

public class ConvertStatementToForRangeIntentionTest extends GoIntentionTestCase {

    @Ignore("broken by the new resolver")
    public void testCallMapExpression() throws Exception {
        doTest();
    }

    @Ignore("broken by the new resolver")
    public void testCallSliceExpression() throws Exception {
        doTest();
    }

    @Ignore("broken by the new resolver")
    public void testVarSliceExpression() throws Exception {
        doTest();
    }

    public void testVariadicSliceExpression() throws Exception {
        doTest();
    }

    @Ignore("broken by the new resolver")
    public void testsubTypeExpression() throws Exception {
        doTest();
    }

    @Ignore("broken by the new resolver")
    public void testsubTypeInRangeValExpression() throws Exception {
        doTest();
    }
}
