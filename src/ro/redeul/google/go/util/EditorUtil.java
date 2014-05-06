package ro.redeul.google.go.util;

import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.IdeActions;
import com.intellij.openapi.application.Result;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.RangeMarker;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.intellij.openapi.editor.actionSystem.EditorActionManager;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.CodeStyleManager;
import org.jetbrains.annotations.NotNull;

public class EditorUtil {
    public static void pressEnterAtLineEnd(Editor editor) {
        Document document = editor.getDocument();
        int line = document.getLineNumber(editor.getCaretModel().getOffset());
        editor.getCaretModel().moveToOffset(document.getLineEndOffset(line));
        pressEnter(editor);
    }

    public static void pressEnter(Editor editor) {
        EditorActionManager manager = EditorActionManager.getInstance();
        EditorActionHandler enterHandler = manager.getActionHandler(IdeActions.ACTION_EDITOR_START_NEW_LINE);
        DataContext dc = DataManager.getInstance().getDataContext(editor.getContentComponent());
        enterHandler.execute(editor, dc);
    }

    public static void reformatLines(@NotNull PsiFile file, Editor editor, int startLine, int endLine) {
        Document doc = editor.getDocument();
        int start = doc.getLineStartOffset(startLine);
        int end = doc.getLineEndOffset(endLine);
        reformatPositions(file, start, end);
    }

    public static void reformatPositions(@NotNull PsiFile file, RangeMarker range) {
        reformatPositions(file, range.getStartOffset(), range.getEndOffset());
    }

    public static void reformatPositions(@NotNull PsiElement element) {
        TextRange range = element.getTextRange();
        reformatPositions(element.getContainingFile(), range.getStartOffset(), range.getEndOffset());
    }

    public static void reformatPositions(@NotNull final PsiFile file, final int start, final int end) {
        if (start >= end) {
            return;
        }

        WriteCommandAction writeCommandAction = new WriteCommandAction(file.getProject(), file) {
            @Override
            protected void run(@NotNull Result result) throws Throwable {
                CodeStyleManager.getInstance(file.getProject()).reformatText(file, start, end);
            }
        };
        writeCommandAction.execute();
    }
}
