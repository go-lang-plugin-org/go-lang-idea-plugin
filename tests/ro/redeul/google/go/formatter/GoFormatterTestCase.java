package ro.redeul.google.go.formatter;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.codeStyle.CodeStyleSettingsManager;
import ro.redeul.google.go.GoLightCodeInsightTestCase;

/**
 * Abstract class formatter test cases
 * <p/>
 * Created on Dec-30-2013 01:15
 *
 * @author <a href="mailto:mtoader@gmail.com">Mihai Toader</a>
 */
public abstract class GoFormatterTestCase extends GoLightCodeInsightTestCase {

    @Override
    protected String getRelativeTestDataPath() {
        return "formatter/";
    }

    @Override
    final protected void _test() {
        String testName = getTestName(true);

        myFixture.configureByFiles(beforeFileName(testName));

        CodeStyleSettingsManager.getSettings(getProject()).SPACE_AROUND_ASSIGNMENT_OPERATORS = true;
        ApplicationManager.getApplication().runWriteAction(new Runnable() {
            @Override
            public void run() {
                CodeStyleManager.getInstance(getProject()).reformat(myFixture.getFile());
            }
        });

        myFixture.checkResultByFile(afterFileName(testName));
    }
}
