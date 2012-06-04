package ro.redeul.google.go;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.PluginPathManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.source.PostprocessReformattingAspect;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
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

        ApplicationManager.getApplication().runWriteAction(new Runnable() {
            @Override
            public void run() {
                invoke(getProject(), myEditor, myFixture.getFile());
                PostprocessReformattingAspect.getInstance(getProject()).doPostponedFormatting();
            }
        });

        result = myEditor.getDocument().getText();
        int caretOffset = myEditor.getCaretModel().getOffset();
        return result.substring(0, caretOffset) + TestUtils.CARET_MARKER + result.substring(caretOffset);
    }

    protected abstract void invoke(Project project, Editor myEditor, PsiFile file);
    protected abstract String getTestDataRelativePath();
}
