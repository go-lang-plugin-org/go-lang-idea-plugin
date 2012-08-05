package ro.redeul.google.go.completion;

import java.util.ArrayList;
import java.util.List;

import com.intellij.codeInsight.completion.CompletionType;
import ro.redeul.google.go.GoLightCodeInsightFixtureTestCase;

public abstract class GoCompletionTestCase
    extends GoLightCodeInsightFixtureTestCase {

    protected String getTestDataRelativePath() {
        return "psi/completion/";
    }


    protected void doTestVariants() {
        myFixture.configureByFile(getTestName(false) + ".go");
        myFixture.completeBasic();
        String fileText = myFixture.getFile().getText();

        List<String> expected = new ArrayList<String>(10);
        int dataPos = fileText.indexOf("/**---");
        if (dataPos != -1) {
            String[] parts = fileText.substring(dataPos + 6).split("[\r\n]+");
            for (String part : parts) {
                part = part.trim();
                if ( ! part.isEmpty() ) {
                    expected.add(part);
                }
            }
        }

        assertOrderedEquals(myFixture.getLookupElementStrings(), expected);
    }

    protected void doTest() {
        myFixture.configureByFile(getTestName(false) + ".go");
        myFixture.complete(CompletionType.BASIC);
        myFixture.checkResultByFile(getTestName(false) + "_after.go", true);
    }
}
