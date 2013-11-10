package ro.redeul.google.go.intentions.parenthesis;

import com.intellij.psi.PsiElement;
import ro.redeul.google.go.lang.psi.declarations.GoConstDeclarations;
import ro.redeul.google.go.lang.psi.declarations.GoVarDeclarations;
import ro.redeul.google.go.lang.psi.toplevel.GoImportDeclarations;

class ParenthesisUtil {
    static PsiElement getRightParenthesis(PsiElement element) {
        PsiElement lastChild = element.getLastChild();
        return lastChild != null && ")".equals(lastChild.getText()) ? lastChild : null;
    }

    static boolean hasOnlyOneDeclaration(PsiElement element) {
        if (element instanceof GoVarDeclarations) {
            return ((GoVarDeclarations) element).getDeclarations().length == 1;
        }

        if (element instanceof GoImportDeclarations) {
            return ((GoImportDeclarations) element).getDeclarations().length == 1;
        }

        return element instanceof GoConstDeclarations && ((GoConstDeclarations) element).getDeclarations().length == 1;
    }

    static PsiElement getDeclaration(PsiElement element) {
        if (element instanceof GoVarDeclarations) {
            return ((GoVarDeclarations) element).getDeclarations()[0];
        }

        if (element instanceof GoImportDeclarations) {
            return ((GoImportDeclarations) element).getDeclarations()[0];
        }

        if (element instanceof GoConstDeclarations) {
            return ((GoConstDeclarations) element).getDeclarations()[0];
        }
        return null;
    }
}
