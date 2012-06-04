package ro.redeul.google.go.lang.parser;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.intellij.openapi.application.PluginPathManager;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.DebugUtil;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import ro.redeul.google.go.util.TestUtils;

public abstract class GoParsingTestCase extends LightCodeInsightFixtureTestCase {

    @Override
    protected String getBasePath() {
        String base = FileUtil.toSystemIndependentName(PluginPathManager.getPluginHomePathRelative("google-go-language")) + "/testdata/";

        return base + File.separator + "parsing" + File.separator + "go";
    }

    protected String getLocalTestDataPath() {
        return getBasePath();
    }

    public void doTest() throws IOException {
        doTest(getTestName(true).replace('$', '/') + ".test");
    }

    private void doTest(String fileName) throws IOException {
        final List<String> list = TestUtils.readInput(getTestDataPath() + "/" + fileName);

        final String input = list.get(0);
        if ( list.size() != 2 || list.get(1).trim().length() == 0 ) {
            dumpParsingTree(input, getTestDataPath() + "/" + fileName);
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
        org.testng.Assert.assertEquals(psiTree.trim(), output.trim());
    }
}
