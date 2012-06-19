package ro.redeul.google.go.inspection;

public class UnusedImportInspectionTest extends GoInspectionTestCase {
    public void testSimple() throws Exception{ doTest(); }
    public void testOnlyOneImport() throws Exception{ doTest(); }
    public void testBlankImport() throws Exception{ doTest(); }
}
