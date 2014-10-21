package ro.redeul.google.go.inspection;

public class RedeclareInspectionTest extends GoInspectionTestCase {
    public void testRedeclare() throws Exception{ doTest(); }

    public void testIssue861() throws Exception{ doTest(); }

    /* TODO FIX TEST
    public void testGH864() throws Exception{ doTest(); }
    */
}
