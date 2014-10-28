package ro.redeul.google.go.inspection;

import org.junit.Ignore;

public class ImportPathInspectionTest extends GoInspectionTestCase {

    public void testSpace() throws Exception{ doTest(); }
    public void testBackslash() throws Exception{ doTest(); }
    public void testEmptyImportPath() throws Exception{ doTest(); }
    public void testCgo() throws Exception{ doTest(); }
    public void testRepeat() throws Exception{ doTest(); }
    public void testNotFound() throws Exception{ doTestWithDirectory(); }

    @Ignore("Broken by new resolver")
    public void testSelf() throws Exception{ doTest(); }

}
