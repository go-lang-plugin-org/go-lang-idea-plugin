package ro.redeul.google.go;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.PluginPathManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.psi.impl.source.PostprocessReformattingAspect;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.util.TestUtils;

import java.util.List;

public abstract class FileDataBasedTest extends LightCodeInsightFixtureTestCase {
    @Override
    protected String getBasePath() {
        String pluginHomePathRelative = PluginPathManager.getPluginHomePathRelative("google-go-language");
        return FileUtil.toSystemIndependentName(pluginHomePathRelative) + "/testdata/" + getTestDataRelativePath();
    }

    protected void doTest() throws Exception {
        final List<String> data = TestUtils.readInput(getTestDataPath() + getTestName(true) + ".test");
        String expected = data.get(1).trim();
        assertEquals(expected, processFile(data.get(0), expected.contains(TestUtils.CARET_MARKER)).trim());
    }

    private String processFile(String fileText, boolean addCaretMarker) {
        String result;

        final GoFile goFile;
        int startOffset = fileText.indexOf(TestUtils.BEGIN_MARKER);
        if (startOffset != -1) {
            fileText = TestUtils.removeBeginMarker(fileText);
            int endOffset = fileText.indexOf(TestUtils.END_MARKER);
            fileText = TestUtils.removeEndMarker(fileText);
            goFile = (GoFile) myFixture.configureByText(GoFileType.GO_FILE_TYPE, fileText);
            myFixture.getEditor().getSelectionModel().setSelection(startOffset, endOffset);
        } else {
            goFile = (GoFile) myFixture.configureByText(GoFileType.GO_FILE_TYPE, fileText);
        }

        final Editor myEditor = myFixture.getEditor();

        ApplicationManager.getApplication().runWriteAction(new Runnable() {
            @Override
            public void run() {
                invoke(getProject(), myEditor, goFile);
                PostprocessReformattingAspect.getInstance(getProject()).doPostponedFormatting();
            }
        });

        result = myEditor.getDocument().getText();
        if (!addCaretMarker) {
            return result;
        }

        int caretOffset = myEditor.getCaretModel().getOffset();
        return result.substring(0, caretOffset) + TestUtils.CARET_MARKER + result.substring(caretOffset);
    }

    protected abstract void invoke(Project project, Editor myEditor, GoFile file);
    protected abstract String getTestDataRelativePath();
}
