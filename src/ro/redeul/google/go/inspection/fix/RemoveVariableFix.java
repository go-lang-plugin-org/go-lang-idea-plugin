package ro.redeul.google.go.inspection.fix;

import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiWhiteSpace;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.lexer.GoTokenTypes;
import ro.redeul.google.go.lang.psi.declarations.GoConstDeclaration;
import ro.redeul.google.go.lang.psi.declarations.GoConstDeclarations;
import ro.redeul.google.go.lang.psi.declarations.GoVarDeclaration;
import ro.redeul.google.go.lang.psi.declarations.GoVarDeclarations;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.expressions.literals.GoIdentifier;

import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.isNodeOfType;

public class RemoveVariableFix implements LocalQuickFix {
    @NotNull
    @Override
    public String getName() {
        return "Remove unused variable";
    }

    @NotNull
    @Override
    public String getFamilyName() {
        return "Variable";
    }

    @Override
    public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
        PsiElement element = descriptor.getPsiElement();
        if (!(element instanceof GoIdentifier)) {
            return;
        }

        GoIdentifier id = (GoIdentifier) element;
        PsiElement parent = id.getParent();
        if (parent instanceof GoVarDeclaration) {
            GoVarDeclaration gsvd = (GoVarDeclaration) parent;
            removeIdentifier(id, parent, gsvd.getIdentifiers(), gsvd.getExpressions());
        } else if (parent instanceof GoConstDeclaration) {
            GoConstDeclaration gcd = (GoConstDeclaration) parent;
            removeIdentifier(id, parent, gcd.getIdentifiers(), gcd.getExpressions());
        }
    }

    private void removeIdentifier(GoIdentifier id, PsiElement parent, GoIdentifier[] ids, GoExpr[] exprs) {
        if (isIdWithDummy(id, ids)) {
            removeWholeElement(parent);
            return;
        }

        int index = identifierIndex(ids, id);
        if (index < 0) {
            return;
        }

        if (index == ids.length - 1) {
            parent.deleteChildRange(ids[index - 1].getNextSibling(), ids[index]);
        } else {
            parent.deleteChildRange(ids[index], ids[index + 1].getPrevSibling());
        }

        if (exprs.length == ids.length) {
            if (index == ids.length - 1) {
                parent.deleteChildRange(exprs[index - 1].getNextSibling(), exprs[index]);
            } else {
                parent.deleteChildRange(exprs[index], exprs[index + 1].getPrevSibling());
            }
        }
    }

    private static boolean isIdWithDummy(GoIdentifier id, GoIdentifier[] ids) {
        for (GoIdentifier i : ids) {
            if (!i.isBlank() && !i.isEquivalentTo(id)) {
                return false;
            }
        }
        return true;
    }

    private static int identifierIndex(GoIdentifier[] ids, GoIdentifier id) {
        for (int i = 0; i < ids.length; i++) {
            if (ids[i].isEquivalentTo(id)) {
                return i;
            }
        }
        return -1;
    }

    private static void removeWholeElement(PsiElement element) {
        if (isOnlyVarDeclaration(element) || isOnlyConstDeclaration(element)) {
            element = element.getParent();
        }

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

    private static boolean isOnlyConstDeclaration(PsiElement e) {
        return e instanceof GoConstDeclaration && e.getParent() instanceof GoConstDeclarations &&
               ((GoConstDeclarations) e.getParent()).getDeclarations().length == 1;
    }

    private static boolean isOnlyVarDeclaration(PsiElement e) {
        return e instanceof GoVarDeclaration && e.getParent() instanceof GoVarDeclarations &&
               ((GoVarDeclarations) e.getParent()).getDeclarations().length == 1;
    }
}
