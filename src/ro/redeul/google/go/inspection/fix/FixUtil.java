package ro.redeul.google.go.inspection.fix;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiWhiteSpace;
import ro.redeul.google.go.lang.lexer.GoTokenTypes;

import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.isNodeOfType;

public class FixUtil {
    public static void removeWholeElement(PsiElement element) {
        PsiElement prev = element.getPrevSibling();
        if (prev instanceof PsiWhiteSpace) {
            prev.delete();
        }

        PsiElement next = element.getNextSibling();
        if (next != null && isNodeOfType(next, GoTokenTypes.wsNLS)) {
            next.delete();
        }

        element.delete();
    }
}
