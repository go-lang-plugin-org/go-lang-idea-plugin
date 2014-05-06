package ro.redeul.google.go.inspection.fix;

import com.intellij.openapi.application.Result;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiWhiteSpace;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.declarations.GoConstDeclaration;
import ro.redeul.google.go.lang.psi.declarations.GoConstDeclarations;
import ro.redeul.google.go.lang.psi.declarations.GoVarDeclaration;
import ro.redeul.google.go.lang.psi.declarations.GoVarDeclarations;

import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.isNewLineNode;

public class FixUtil {
    public static void removeWholeElement(final PsiElement element) {
        WriteCommandAction writeCommandAction = new WriteCommandAction(element.getContainingFile().getProject()) {
            @Override
            protected void run(@NotNull Result result) throws Throwable {
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
        };
        writeCommandAction.execute();
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
