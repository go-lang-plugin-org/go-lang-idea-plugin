package ro.redeul.google.go.completion;

import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.psi.PsiFile;
import org.junit.Ignore;
import ro.redeul.google.go.GoLightCodeInsightFixtureTestCase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class GoCompletionTestCase extends GoLightCodeInsightFixtureTestCase {

    protected String getTestDataRelativePath() {
        return "psi/completion/";
    }

    @Ignore
    protected void _testVariants() throws IOException {

        addPackageBuiltin();

        PsiFile testFile = myFixture.configureByFile(getTestName(false) + ".go");

        // find the expected outcome
        String fileText = testFile.getText();
        List<String> expected = new ArrayList<String>();
        int dataPos = fileText.indexOf("/**---");
        if (dataPos != -1) {
            String[] parts = fileText.substring(dataPos + 6).trim().split("[\r\n]+");
            for (String part : parts) {
                part = part.trim();
                if (!part.isEmpty()) {
                    expected.add(part);
                }
            }
        }

        // do the completion
        myFixture.completeBasic();

        List<String> lookupStrings = myFixture.getLookupElementStrings();
        assertNotNull("We should have multiple completion variants", lookupStrings);
        assertOrderedEquals("The completions should be the expected ones", lookupStrings, expected);
    }

    @Ignore
    protected void _testSingleCompletion() throws IOException {
        addPackageBuiltin();
        myFixture.configureByFile(getTestName(false) + ".go");
        myFixture.complete(CompletionType.BASIC);
        myFixture.checkResultByFile(getTestName(false) + "_after.go", true);
    }
}
