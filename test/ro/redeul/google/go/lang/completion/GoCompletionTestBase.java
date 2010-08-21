package ro.redeul.google.go.lang.completion;

import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.testFramework.UsefulTestCase;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import ro.redeul.google.go.GoFileType;
import ro.redeul.google.go.util.TestUtils;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: mtoader
 * Date: Aug 20, 2010
 * Time: 5:26:05 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class GoCompletionTestBase extends LightCodeInsightFixtureTestCase {

    @Override
    protected String getBasePath() {
        return TestUtils.getTestDataPath() + "completion/go/";
    }

    protected void doBasicTest() {
        String testName = getBasePath() + getTestName(false);
        myFixture.testCompletion(testName + ".go", testName + "_after.go");
    }

    protected void doSmartTest() {
        myFixture.configureByFile(getTestName(false) + ".go");
        myFixture.complete(CompletionType.SMART);
        myFixture.checkResultByFile(getTestName(false) + "_after.go", true);
    }

    public void doSmartCompletion(String... variants) throws Exception {
        myFixture.configureByFile(getTestName(false) + ".go");
        myFixture.complete(CompletionType.SMART);
        final List<String> list = myFixture.getLookupElementStrings();

        assertNotNull(list);
        UsefulTestCase.assertSameElements(list, variants);
    }

    public void doVariantsTest(String... variants) throws Throwable {
        myFixture.configureByFile(getTestName(false) + ".go");
        myFixture.complete(CompletionType.BASIC);

        assertOrderedEquals(myFixture.getLookupElementStrings(), variants);
    }

    public void doTest() throws Exception {
       final List<String> data = TestUtils.readInput(getTestDataPath() + getTestName(true) + ".test");

       myFixture.configureByText(GoFileType.GO_FILE_TYPE, data.get(0));

//       final List<SmartEnterProcessor> processors = getSmartProcessors(GroovyFileType.GROOVY_LANGUAGE);
//       new WriteCommandAction(getProject()) {
//         protected void run(Result result) throws Throwable {
//           final Editor editor = myFixture.getEditor();
//           for (SmartEnterProcessor processor : processors) {
//             processor.process(getProject(), editor, myFixture.getFile());
//           }
//
//         }
//       }.execute();
       myFixture.checkResult(data.get(1));
     }
    
}
