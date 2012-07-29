package ro.redeul.google.go.lang.parser;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;

import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.DebugUtil;
import org.junit.Assert;
import ro.redeul.google.go.GoLightCodeInsightFixtureTestCase;
import ro.redeul.google.go.util.GoTestUtils;

public abstract class GoParsingTestCase
    extends GoLightCodeInsightFixtureTestCase
{
    @Override
    protected String getTestDataRelativePath() {
        return "parsing" + File.separator;
    }

    public void doTest() throws IOException {
        doTest(getTestName(true).replaceAll("_+", Matcher.quoteReplacement(File.separator)));
    }

    private void doTest(String fileName) throws IOException {

        List<String> list = null;

        try {
            list = GoTestUtils.readInput(getTestDataPath() + File.separator + fileName + ".go");
        } catch (IOException e) {
            list = GoTestUtils.readInput(getTestDataPath() + File.separator + fileName + ".test");
        }

        final String input = list.get(0);
        if ( list.size() != 2 || list.get(1).trim().length() == 0 ) {
            dumpParsingTree(input, getTestDataPath() + File.separator + fileName + ".go");
        } else {
            checkParsing(input, list.get(1).trim());
        }
    }

    protected void dumpParsingTree(String input, String fileName) throws IOException {
        final PsiFile psiFile =
            GoTestUtils.createPseudoPhysicalGoFile(getProject(), input);

        String psiTree = DebugUtil.psiToString(psiFile, false);

        GoTestUtils.writeTestFile(input, psiTree.trim(), fileName);
    }

    protected void checkParsing(String input, String output) {
        final PsiFile psiFile = GoTestUtils.createPseudoPhysicalGoFile(
            getProject(), input);
        String psiTree = DebugUtil.psiToString(psiFile, false);
        Assert.assertEquals(psiTree.trim(), output.trim());
    }
}
