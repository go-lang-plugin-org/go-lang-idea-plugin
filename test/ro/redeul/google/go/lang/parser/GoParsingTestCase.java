package ro.redeul.google.go.lang.parser;

import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.DebugUtil;
import ro.redeul.google.go.GoLightCodeInsightFixtureTestCase;
import ro.redeul.google.go.util.GoTestUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

public abstract class GoParsingTestCase
    extends GoLightCodeInsightFixtureTestCase
{
    @Override
    protected String getTestDataRelativePath() {
        return "parsing/";
    }

    public void doTest() throws IOException {
        doTest(getTestName(true).replaceAll("_+", File.separator) + ".test");
    }

    private void doTest(String fileName) throws IOException {
        final List<String> list =
            GoTestUtils.readInput(getTestDataPath() + "/" + fileName);

        final String input = list.get(0);
        if ( list.size() != 2 || list.get(1).trim().length() == 0 ) {
            dumpParsingTree(input, getTestDataPath() + "/" + fileName);
        } else {
            checkParsing(input, list.get(1).trim());
        }
    }

    protected void dumpParsingTree(String input, String fileName) throws IOException {
        final PsiFile psiFile = GoTestUtils.createPseudoPhysicalGoFile(
            getProject(), input);
        String psiTree = DebugUtil.psiToString(psiFile, false);

        GoTestUtils.writeTestFile(input, psiTree.trim(), fileName);
    }

    protected void checkParsing(String input, String output) {
        final PsiFile psiFile = GoTestUtils.createPseudoPhysicalGoFile(
            getProject(), input);
        String psiTree = DebugUtil.psiToString(psiFile, false);
        org.testng.Assert.assertEquals(psiTree.trim(), output.trim());
    }
}
