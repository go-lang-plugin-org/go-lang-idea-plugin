package ro.redeul.google.go.lang.completion.smartEnter.fixers;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiErrorElement;
import ro.redeul.google.go.lang.psi.statements.GoBlockStatement;
import ro.redeul.google.go.lang.psi.utils.GoPsiUtils;

import static ro.redeul.google.go.util.EditorUtil.pressEnterAtLineEnd;
import static ro.redeul.google.go.util.EditorUtil.reformatLines;

class FixerUtil {
    public static void addEmptyBlockAtTheEndOfElement(Editor editor, PsiElement element, String content) {
        Document doc = editor.getDocument();
        PsiElement lastChild = GoPsiUtils.getPrevSiblingIfItsWhiteSpaceOrComment(element.getLastChild());
        if (lastChild instanceof PsiErrorElement) {
            lastChild = lastChild.getPrevSibling();
        }

        if (lastChild != null) {
            int offset = lastChild.getTextRange().getEndOffset();
            int line = doc.getLineNumber(offset);
            doc.insertString(offset, content);
            editor.getCaretModel().moveToOffset(offset);
            reformatLines(lastChild.getContainingFile(), editor, line, line + 1);
            pressEnterAtLineEnd(editor);
        }
    }

    public static void addEmptyBlockAtTheEndOfElement(Editor editor, PsiElement element) {
        addEmptyBlockAtTheEndOfElement(editor, element, " {\n}");
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
