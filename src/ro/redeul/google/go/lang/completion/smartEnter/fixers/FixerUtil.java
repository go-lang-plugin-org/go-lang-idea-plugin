package ro.redeul.google.go.lang.completion.smartEnter.fixers;

import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.IdeActions;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.intellij.openapi.editor.actionSystem.EditorActionManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.CodeStyleManager;
import ro.redeul.google.go.lang.psi.statements.GoBlockStatement;
import ro.redeul.google.go.lang.psi.utils.GoPsiUtils;

class FixerUtil {
    public static void pressEnterAtLineEnd(Editor editor) {
        Document document = editor.getDocument();
        int line = document.getLineNumber(editor.getCaretModel().getOffset());
        editor.getCaretModel().moveToOffset(document.getLineEndOffset(line));

        EditorActionManager manager = EditorActionManager.getInstance();
        EditorActionHandler enterHandler = manager.getActionHandler(IdeActions.ACTION_EDITOR_START_NEW_LINE);
        DataContext dc = DataManager.getInstance().getDataContext(editor.getContentComponent());
        enterHandler.execute(editor, dc);
    }

    public static void addEmptyBlockAtTheEndOfElement(Editor editor, PsiElement element) {
        Document doc = editor.getDocument();
        PsiElement lastChild = GoPsiUtils.getPrevSiblingIfItsWhiteSpaceOrComment(element.getLastChild());
        if (lastChild != null) {
            doc.insertString(lastChild.getTextRange().getEndOffset(), " {\n}");
            pressEnterAtLineEnd(editor);

            int offset = editor.getCaretModel().getOffset();
            int line = doc.getLineNumber(offset);
            int start = doc.getLineStartOffset(line - 1);
            int end = doc.getLineEndOffset(line + 1);
            final PsiFile file = element.getContainingFile();
            final PsiFile baseFile = file.getViewProvider().getPsi(file.getViewProvider().getBaseLanguage());
            CodeStyleManager.getInstance(editor.getProject()).reformatText(baseFile, start, end);
        }
    }

    public static boolean elementHasBlockChild(PsiElement element) {
        if (element == null) {
            return false;
        }

        element = element.getFirstChild();
        while (element != null) {
            if (element instanceof GoBlockStatement) {
                return true;
            }

            element = element.getNextSibling();
        }
        return false;
    }
}
