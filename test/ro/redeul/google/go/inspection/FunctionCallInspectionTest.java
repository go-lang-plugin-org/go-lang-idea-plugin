package ro.redeul.google.go.inspection;

public class FunctionCallInspectionTest extends GoInspectionTestCase {
    public void testSimple() throws Exception{ doTest(); }
    public void testMake() throws Exception{ doTest(); }
    public void testNew() throws Exception{ doTest(); }
    public void testFuncCall() throws Exception{ doTest(); }
    public void testBuiltinCall() throws Exception{
        myFixture.addFileToProject("builtin.go", "package builtin\ntype Type int\ntype Type1 int\nfunc append(slice []Type, elems ...Type) []Type\nfunc copy(dst, src []Type) int\nfunc delete(m map[Type]Type1, key Type)\n");
        doTest();
    }
}
