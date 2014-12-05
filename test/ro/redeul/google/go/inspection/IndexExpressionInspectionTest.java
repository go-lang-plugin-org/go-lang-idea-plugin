package ro.redeul.google.go.inspection;

import org.junit.Ignore;

public class IndexExpressionInspectionTest extends GoInspectionTestCase {

    public void testSimple() throws Exception{ doTest(); }

    @Ignore("failing test because len(constant) is not returning proper values")
    public void testString() throws Exception{ doTest(); }
}
