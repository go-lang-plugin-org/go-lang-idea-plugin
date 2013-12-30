import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.codeStyle.CodeStyleSettingsManager;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;

import java.util.List;

/**
 * TODO: Document this
 * <p/>
 * Created on Dec-30-2013 00:39
 *
 * @author <a href="mailto:mtoader@gmail.com">Mihai Toader</a>
 */
public class SimpleCompletionTest extends LightCodeInsightFixtureTestCase {

    @Override
    protected String getTestDataPath() {
        return "testdata";
    }

    public void testCompletion() {
        myFixture.configureByFiles("CompleteTestData.go");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> strings = myFixture.getLookupElementStrings();
//        assertTrue(strings.containsAll(Arrays.asList("key\\ with\\ spaces", "language", "message", "tab", "website")));
//        assertEquals(5, strings.size());
    }

    public void testFormatter() {
        myFixture.configureByFiles("FormatterTestData.go");
        CodeStyleSettingsManager.getSettings(getProject()).SPACE_AROUND_ASSIGNMENT_OPERATORS = true;
        ApplicationManager.getApplication().runWriteAction(new Runnable() {
            @Override
            public void run() {
                CodeStyleManager.getInstance(getProject()).reformat(myFixture.getFile());
            }
        });
        myFixture.checkResultByFile("DefaultTestData.simple");
    }
}