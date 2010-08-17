package ro.redeul.google.go.lang.parser;

import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.DebugUtil;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import ro.redeul.google.go.util.TestUtils;

import java.io.IOException;
import java.util.List;

public abstract class GoParsingTestCase extends LightCodeInsightFixtureTestCase {

    @Override
    protected String getBasePath() {
        return TestUtils.getTestDataPath() + "parsing/go/";
    }

    public void doTest() throws IOException {
        String testName = getTestName(true);

        boolean isFirst = true;

        for ( String s : testName.split("(?<=\\p{Lower})(?=\\p{Upper})") )
        {
            if ( isFirst ) {
                testName = s.toLowerCase();
                isFirst = false;
            } else {
                testName += "/" + s.toLowerCase();
            }
        }

        doTest(testName + ".test");
    }

    protected void doTest(String fileName) throws IOException {
        final List<String> list = TestUtils.readInput(getBasePath() + "/" + fileName);

        final String input = list.get(0);
        if ( list.size() != 2 || list.get(1).trim().length() == 0 ) {
            dumpParsingTree(input, getBasePath() + "/" + fileName);
        } else {
            checkParsing(input, list.get(1).trim());
        }
    }

    protected void dumpParsingTree(String input, String fileName) throws IOException {
        final PsiFile psiFile = TestUtils.createPseudoPhysicalGoFile(getProject(), input);
        String psiTree = DebugUtil.psiToString(psiFile, false);

        TestUtils.writeTestFile(input, psiTree.trim(), fileName);
    }

    protected void checkParsing(String input, String output) {
        final PsiFile psiFile = TestUtils.createPseudoPhysicalGoFile(getProject(), input);
        String psiTree = DebugUtil.psiToString(psiFile, false);
        assertEquals(output.trim(), psiTree.trim());
    }
}
