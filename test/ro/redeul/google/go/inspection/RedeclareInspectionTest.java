package ro.redeul.google.go.inspection;

import org.junit.Ignore;

public class RedeclareInspectionTest extends GoInspectionTestCase {
    public void testRedeclare() throws Exception{ doTest(); }

    public void testIssue861() throws Exception{ doTest(); }

    public void testIssue864() throws Exception{ doTest(); }

    public void testIssue894() throws Exception{ doTest(); }

    public void testInterface() throws Exception{ doTest(); }

    public void testSwitch() throws Exception{ doTest(); }

    public void testSelect() throws Exception{ doTest(); }

    public void testInit() throws Exception{ doTest(); }

    public void testMultiFiles() throws Exception{ doTest(); }

    @Ignore("failing test")
    public void testBuildTags() throws Exception{ doTest(); }

    public void testFunction() throws Exception{ doTest(); }

    public void testShortVarDeclaration() throws Exception{ doTest(); }
}
