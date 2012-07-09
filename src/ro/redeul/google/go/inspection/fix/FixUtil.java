package ro.redeul.google.go.inspection.fix;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiWhiteSpace;
import ro.redeul.google.go.lang.psi.declarations.GoConstDeclaration;
import ro.redeul.google.go.lang.psi.declarations.GoConstDeclarations;
import ro.redeul.google.go.lang.psi.declarations.GoVarDeclaration;
import ro.redeul.google.go.lang.psi.declarations.GoVarDeclarations;

import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.isNewLineNode;

public class FixUtil {
    public static void removeWholeElement(PsiElement element) {
        PsiElement prev = element.getPrevSibling();
        if (prev instanceof PsiWhiteSpace) {
            prev.delete();
        }

        PsiElement next = element.getNextSibling();
        if (next != null && isNewLineNode(next)) {
            next.delete();
        }

        element.delete();
    }

    static boolean isOnlyConstDeclaration(PsiElement e) {
        return e instanceof GoConstDeclaration && e.getParent() instanceof GoConstDeclarations &&
               ((GoConstDeclarations) e.getParent()).getDeclarations().length == 1;
    }

    static boolean isOnlyVarDeclaration(PsiElement e) {
        return e instanceof GoVarDeclaration && e.getParent() instanceof GoVarDeclarations &&
               ((GoVarDeclarations) e.getParent()).getDeclarations().length == 1;
    }
}
