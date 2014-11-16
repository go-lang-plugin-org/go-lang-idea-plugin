package ro.redeul.google.go.inspection;

import org.junit.Ignore;

public class IndexExpressionInspectionTest extends GoInspectionTestCase {

    @Ignore("broken by function call inspection refactoring")
    public void testSimple() throws Exception{ doTest(); }
}
