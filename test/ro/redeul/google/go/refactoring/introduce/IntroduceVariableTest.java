package ro.redeul.google.go.refactoring.introduce;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.PluginPathManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.psi.impl.source.PostprocessReformattingAspect;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import ro.redeul.google.go.GoFileType;
import ro.redeul.google.go.util.TestUtils;

import java.util.List;

public class IntroduceVariableTest extends LightCodeInsightFixtureTestCase {

    @Override
    protected String getBasePath() {
        String pluginHomePathRelative = PluginPathManager.getPluginHomePathRelative("google-go-language");
        String base = FileUtil.toSystemIndependentName(pluginHomePathRelative) + "/testdata/";
        return base + "refactoring/introduce/variable/";
    }

    public void testSimple() throws Exception { doTest(); }
    public void testParenthesis1() throws Exception { doTest(); }
    public void testParenthesis2() throws Exception { doTest(); }
    public void testParenthesis3() throws Exception { doTest(); }
    public void testParenthesis4() throws Exception { doTest(); }

    private void doTest() throws Exception {
        final List<String> data = TestUtils.readInput(getTestDataPath() + getTestName(true) + ".test");
        assertEquals(data.get(1).trim(), processFile(data.get(0)).trim());
    }

    private String processFile(String fileText) {
        String result;
        int startOffset = fileText.indexOf(TestUtils.BEGIN_MARKER);
        fileText = TestUtils.removeBeginMarker(fileText);
        int endOffset = fileText.indexOf(TestUtils.END_MARKER);
        fileText = TestUtils.removeEndMarker(fileText);
        myFixture.configureByText(GoFileType.GO_FILE_TYPE, fileText);

        final Editor myEditor = myFixture.getEditor();

        myEditor.getSelectionModel().setSelection(startOffset, endOffset);

        // gathering data for introduce variable
        final GoIntroduceVariableHandler handler = new GoIntroduceVariableHandler();
        ApplicationManager.getApplication().runWriteAction(new Runnable() {
            @Override
            public void run() {
                handler.invoke(getProject(), myEditor, myFixture.getFile(), null);
                PostprocessReformattingAspect.getInstance(getProject()).doPostponedFormatting();
            }
        });

        result = myEditor.getDocument().getText();
        int caretOffset = myEditor.getCaretModel().getOffset();
        return result.substring(0, caretOffset) + TestUtils.CARET_MARKER + result.substring(caretOffset);
    }
}
