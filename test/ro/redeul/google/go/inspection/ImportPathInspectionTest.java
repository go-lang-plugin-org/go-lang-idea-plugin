package ro.redeul.google.go.inspection;

public class ImportPathInspectionTest extends GoInspectionTestCase {

    public void testSpace() throws Exception{ doTest(); }
    public void testBackslash() throws Exception{ doTest(); }
    public void testEmptyImportPath() throws Exception{ doTest(); }
    public void testCgo() throws Exception{ doTest(); }
    public void testRepeat() throws Exception{ doTest(); }
    public void testNotFound() throws Exception{ doTest(); }
    public void testSelf() throws Exception{ doTest(); }

}
