package ro.redeul.google.go.inspection;

public class UnusedVariableInspectionTest
    extends GoInspectionTestCase<UnusedVariableInspection>
{
    public UnusedVariableInspectionTest() {
        super(UnusedVariableInspection.class);
    }

    public void testSimple() throws Exception{ doTest(); }
    public void testForScope() throws Exception{ doTest(); }
    public void testTypeFields() throws Exception{ doTest(); }
    public void testUnusedConst() throws Exception{ doTest(); }
    public void testUnusedParameter() throws Exception{ doTest(); }
    public void testLocalHidesGlobal() throws Exception{ doTest(); }
    public void testFunction() throws Exception{ doTest(); }
    public void testInterface() throws Exception{ doTest(); }
    public void testIota() throws Exception{ doTest(); }

}
