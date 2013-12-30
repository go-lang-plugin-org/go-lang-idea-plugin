package ro.redeul.google.go;

import java.util.List;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.codeStyle.CodeStyleSettingsManager;
import com.intellij.psi.codeStyle.CommonCodeStyleSettings;
import com.intellij.psi.impl.source.PostprocessReformattingAspect;
import org.junit.Assert;
import org.junit.Ignore;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.util.GoTestUtils;

@Ignore
public abstract class GoEditorAwareTestCase
        extends GoLightCodeInsightFixtureTestCase {

    protected void doTest() throws Exception {
        List<String> data = GoTestUtils.readInput(getTestFileName());

        String expected = data.get(1);

        Assert.assertEquals(expected,
                processFile(data.get(0),
                        expected.contains(
                                GoTestUtils.MARKER_CARET)));
    }

    private String processFile(String fileText, boolean addCaretMarker) {
        final GoFile myFile = createGoFile(fileText);
        final Editor myEditor = myFixture.getEditor();
        final Project myProject = getProject();

        CodeStyleSettings settings =
                CodeStyleSettingsManager.getInstance(myProject).getCurrentSettings();

        CommonCodeStyleSettings commonSettings =
                settings.getCommonSettings(GoLanguage.INSTANCE);

        if (commonSettings != null) {
            CommonCodeStyleSettings.IndentOptions indentOptions =
                    commonSettings.getIndentOptions();

            if (indentOptions != null)
                indentOptions.USE_TAB_CHARACTER = true;
        }

        ApplicationManager.getApplication().runWriteAction(new Runnable() {
            @Override
            public void run() {
                invoke(myProject, myEditor, myFile);
                PostprocessReformattingAspect.getInstance(myProject).doPostponedFormatting();
            }
        });

        String result = myEditor.getDocument().getText();
        if (!addCaretMarker) {
            return result;
        }

        int caretOffset = myEditor.getCaretModel().getOffset();
        return result.substring(0,
                caretOffset) + GoTestUtils.MARKER_CARET + result
                .substring(caretOffset);
    }

    private GoFile createGoFile(String fileText) {
        GoFile goFile;
        int startOffset = fileText.indexOf(GoTestUtils.MARKER_BEGIN);
        if (startOffset != -1) {
            fileText = GoTestUtils.removeBeginMarker(fileText);
            int endOffset = fileText.indexOf(GoTestUtils.MARKER_END);
            fileText = GoTestUtils.removeEndMarker(fileText);
            goFile = (GoFile) myFixture.configureByText(GoFileType.INSTANCE,
                    fileText);
            myFixture.getEditor()
                    .getSelectionModel()
                    .setSelection(startOffset, endOffset);
            myFixture.getEditor().getCaretModel().moveToOffset(endOffset);
        } else {
            goFile = (GoFile) myFixture.configureByText(GoFileType.INSTANCE,
                    fileText);
        }
        return goFile;
    }

    protected abstract void invoke(Project project, Editor editor, GoFile file);
}
