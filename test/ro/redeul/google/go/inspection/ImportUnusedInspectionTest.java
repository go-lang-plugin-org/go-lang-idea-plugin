package ro.redeul.google.go.inspection;

public class ImportUnusedInspectionTest extends GoInspectionTestCase {

    public void testSimple() throws Exception{ doTestWithDirectory(); }
    public void testOnlyOneImport() throws Exception{ doTestWithDirectory(); }
    public void testDotImport() throws Exception{ doTestWithDirectory(); }

    public void testBlankImport() throws Exception{ doTest(); }
    public void testMixedCaseImport() throws Exception{ doTest(); }
    public void testCgo() throws Exception{ doTest(); }
}
