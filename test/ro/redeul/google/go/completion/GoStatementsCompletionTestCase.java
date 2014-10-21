package ro.redeul.google.go.completion;

import java.io.IOException;

public class GoStatementsCompletionTestCase extends GoCompletionTestCase {
    protected String getTestDataRelativePath() {
        return super.getTestDataRelativePath() + "statements";
    }

    public void testConstDeclaration() throws IOException { doTest(); }

    public void testVarDeclaration() throws IOException { doTest(); }

    public void testSwitch() throws IOException { doTest(); }

    public void testFor() throws IOException {
        doTest();
    }

    public void testIf() throws IOException {
        doTest();
    }

    public void testGo() throws IOException { doTest(); }

    public void testGoFunc() throws IOException { doTest(); }

    public void testDefer() throws IOException { doTest(); }

    public void testDeferFunc() throws IOException {
        doTest();
    }

    public void testSelect() throws IOException {
        doTest();
    }

    public void testContinue() throws IOException {
        doTest();
    }

    public void testBreak() throws IOException {
        doTest();
    }

    public void testFieldsViaForRangeWithArray() throws IOException {
        doTestVariants();
    }

    public void testFieldsViaForRangeWithArrayPointer() throws IOException {
        doTestVariants();
    }

    public void testFieldsViaForRangeWithSlice() throws IOException {
        doTestVariants();
    }

    public void testMapKeyViaForRange() throws IOException {
        doTestVariants();
    }

    public void testMapValueViaForRange() throws IOException {
        doTestVariants();
    }

    public void testChannelTypeViaForRange() throws IOException {
        doTestVariants();
    }
}
