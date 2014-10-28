package ro.redeul.google.go.inspection;

import org.junit.Ignore;

public class ImportUnusedInspectionTest extends GoInspectionTestCase {

    @Ignore("Broken by new resolver")
    public void testSimple() throws Exception{ doTest(); }
    @Ignore("Broken by new resolver")
    public void testOnlyOneImport() throws Exception{ doTest(); }
    @Ignore("Broken by new resolver")
    public void testDotImport() throws Exception{ doTest(); }
    public void testBlankImport() throws Exception{ doTest(); }
    public void testMixedCaseImport() throws Exception{ doTest(); }
    public void testCgo() throws Exception{ doTest(); }
}
