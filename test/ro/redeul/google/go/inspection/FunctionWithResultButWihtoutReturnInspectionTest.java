package ro.redeul.google.go.inspection;

import org.junit.Ignore;

public class FunctionWithResultButWihtoutReturnInspectionTest extends GoInspectionTestCase {
    public void testWithoutReturn() throws Exception{ doTest(); }

    @Ignore("failing test")
    public void testSwitchFallThrough() throws Exception{ doTest(); }

}
