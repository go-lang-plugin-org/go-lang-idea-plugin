package ro.redeul.google.go.inspection;

public class VarDeclarationInspectionTest
    extends GoInspectionTestCase<VarDeclarationInspection>
{
    public VarDeclarationInspectionTest() {
        super(VarDeclarationInspection.class);
    }

    public void testAssignmentCountMismatch() throws Exception{ doTest(); }
}
