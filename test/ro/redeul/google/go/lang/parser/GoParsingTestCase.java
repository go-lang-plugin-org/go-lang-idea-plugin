package ro.redeul.google.go.lang.parser;

import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.DebugUtil;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import ro.redeul.google.go.util.TestUtils;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;

public abstract class GoParsingTestCase extends LightCodeInsightFixtureTestCase {

    String name = null;

    @BeforeMethod
    public void before(Method m) throws Exception {
        name = m.getName();
        setUp();
    }

    @AfterMethod
    public void after(Method m) throws Exception {
        name = null;
        tearDown();
    }


    @Override
    protected String getBasePath() {
        return TestUtils.getTestDataPath() + "parsing/go/";
    }

    @Override
    public String getName() {
        return super.getName() == null ? name : super.getName(); 
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
        org.testng.Assert.assertEquals(psiTree.trim(), output.trim());        
    }
}
