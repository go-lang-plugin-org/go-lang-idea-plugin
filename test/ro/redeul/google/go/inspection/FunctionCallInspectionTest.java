package ro.redeul.google.go.inspection;

public class FunctionCallInspectionTest extends GoInspectionTestCase {

    public void testSimple() throws Exception { doTest(); }

    public void testMake() throws Exception { doTest(); }

    public void testNew() throws Exception { doTest(); }

    public void testCopy() throws Exception { doTest(); }

    public void testFuncCall() throws Exception { doTest(); }

    public void testAppend() throws Exception { doTest(); }

    public void testDelete() throws Exception { doTest(); }

    public void testIssue812() throws Exception { doTest(); }

    public void testIssue856() throws Exception { doTest(); }

    public void testInterface() throws Exception { doTest(); }

    public void testIssue875() throws Exception { doTest(); }

    public void testIssue686() throws Exception { doTest(); }

    public void testEmptyInterfaceImplementedByString() throws Exception { doTest(); }

    public void testPanic() throws Exception { doTest(); }

    public void testClose() throws Exception { doTest(); }

    public void testLen() throws Exception { doTest(); }

    // see #1037
    public void testDontCrashOnCast() throws Exception { doTest(); }

    public void testTypeCast() throws Exception { doTest(); }
}
