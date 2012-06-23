package ro.redeul.google.go.lang.completion.smartEnter.fixers;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import ro.redeul.google.go.lang.psi.statements.GoBlockStatement;
import ro.redeul.google.go.lang.psi.utils.GoPsiUtils;

import static ro.redeul.google.go.util.EditorUtil.pressEnterAtLineEnd;

class FixerUtil {
    public static void addEmptyBlockAtTheEndOfElement(Editor editor, PsiElement element) {
        Document doc = editor.getDocument();
        PsiElement lastChild = GoPsiUtils.getPrevSiblingIfItsWhiteSpaceOrComment(element.getLastChild());
        if (lastChild != null) {
            doc.insertString(lastChild.getTextRange().getEndOffset(), " {\n}");
            pressEnterAtLineEnd(editor);
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
