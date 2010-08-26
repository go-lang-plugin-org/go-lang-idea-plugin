package ro.redeul.google.go.lang.completion;

import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.testFramework.UsefulTestCase;
import org.testng.Assert;
import ro.redeul.google.go.lang.GoCodeInsightTestCase;

import java.io.File;
import java.util.List;

public abstract class GoCompletionTestBase extends GoCodeInsightTestCase {

    @Override
    protected String getRelativeDataPath() {
        return "completion" + File.separator + "go";
    }

    protected void doBasicTest(String ... otherFiles) {
        String testName = getTestName();


        String sourceFiles[] = new String[1 + otherFiles.length];

        sourceFiles[0] = testName + ".go";
        for (int i = 0, otherFilesLength = otherFiles.length; i < otherFilesLength; i++) {
            String otherFile = otherFiles[i];
            sourceFiles[1 + i] = testName + "_" + otherFile + ".go";
        }

        fixture.testCompletion(sourceFiles, testName + "_after.go");
    }

    protected void doSmartTest() {
        fixture.configureByFile(getTestName() + ".go");
        fixture.complete(CompletionType.SMART);
        fixture.checkResultByFile(getTestName() + "_after.go", true);
    }

    public void doSmartCompletion(String... variants) throws Exception {
        fixture.configureByFile(getTestName() + ".go");
        fixture.complete(CompletionType.SMART);
        final List<String> list = fixture.getLookupElementStrings();

        Assert.assertNotNull(list);
        UsefulTestCase.assertSameElements(list, variants);
    }

    public void doVariantsTest(String... variants) throws Throwable {
        fixture.configureByFile(getTestName(false) + ".go");
        fixture.complete(CompletionType.BASIC);


        assertOrderedEquals(fixture.getLookupElementStrings(), variants);
    }

    public void doTest() throws Exception {
//        final List<String> data = TestUtils.readInput(getTestCaseDataPath() + getTestName(true) + ".test");

//        fixture.configureByText(GoFileType.GO_FILE_TYPE, data.get(0));

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
//        fixture.checkResult(data.get(1));
    }

}
