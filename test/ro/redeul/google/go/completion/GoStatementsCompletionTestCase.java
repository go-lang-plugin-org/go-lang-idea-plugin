package ro.redeul.google.go.completion;

import java.io.IOException;

public class GoStatementsCompletionTestCase extends GoCompletionTestCase {
    protected String getTestDataRelativePath() {
        return super.getTestDataRelativePath() + "statements";
    }

    public void testConstDeclaration() throws IOException { _testSingleCompletion(); }

    public void testVarDeclaration() throws IOException { _testSingleCompletion(); }

    public void testSwitch() throws IOException { _testSingleCompletion(); }

    public void testFor() throws IOException {
        _testSingleCompletion();
    }

    public void testIf() throws IOException {
        _testSingleCompletion();
    }

    public void testGo() throws IOException { _testSingleCompletion(); }

    public void testGoFunc() throws IOException { _testSingleCompletion(); }

    public void testDefer() throws IOException { _testSingleCompletion(); }

    public void testDeferFunc() throws IOException {
        _testSingleCompletion();
    }

    public void testSelect() throws IOException {
        _testSingleCompletion();
    }

    public void testContinue() throws IOException {
        _testSingleCompletion();
    }

    public void testBreak() throws IOException {
        _testSingleCompletion();
    }

    public void testFieldsViaForRangeWithArray() throws IOException {
        _testVariants();
    }

    public void testFieldsViaForRangeWithArrayPointer() throws IOException {
        _testVariants();
    }

    public void testFieldsViaForRangeWithSlice() throws IOException {
        _testVariants();
    }

    public void testMapKeyViaForRange() throws IOException {
        _testVariants();
    }

    public void testMapValueViaForRange() throws IOException {
        _testVariants();
    }

    public void testChannelTypeViaForRange() throws IOException {
        _testVariants();
    }
}
