package ro.redeul.google.go.inspection.fix;

import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.application.Result;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.declarations.GoConstDeclaration;
import ro.redeul.google.go.lang.psi.declarations.GoVarDeclaration;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;

import static ro.redeul.google.go.inspection.fix.FixUtil.removeWholeElement;

public class RemoveVariableFix implements LocalQuickFix {
    @NotNull
    @Override
    public String getName() {
        return "Remove unused variable";
    }

    @NotNull
    @Override
    public String getFamilyName() {
        return "Go Unused Symbols";
    }

    @Override
    public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
        applyFix(descriptor.getPsiElement());
    }

    public void applyFix(@NotNull PsiElement element) {
        if (!(element instanceof GoLiteralIdentifier)) {
            return;
        }

        GoLiteralIdentifier id = (GoLiteralIdentifier) element;
        PsiElement parent = id.getParent();
        if (parent instanceof GoVarDeclaration) {
            GoVarDeclaration gsvd = (GoVarDeclaration) parent;
            removeIdentifier(id, parent, gsvd.getIdentifiers(), gsvd.getExpressions());
        } else if (parent instanceof GoConstDeclaration) {
            GoConstDeclaration gcd = (GoConstDeclaration) parent;
            removeIdentifier(id, parent, gcd.getIdentifiers(), gcd.getExpressions());
        }
    }

    private void removeIdentifier(GoLiteralIdentifier id, PsiElement parent, final GoLiteralIdentifier[] ids, final GoExpr[] exprs) {
        if (isIdWithBlank(id, ids)) {
            if (FixUtil.isOnlyVarDeclaration(parent) || FixUtil.isOnlyConstDeclaration(parent)) {
                parent = parent.getParent();
            }
            removeWholeElement(parent);
            return;
        }

        final int index = identifierIndex(ids, id);
        if (index < 0) {
            return;
        }

        final PsiElement finalParent = parent;
        WriteCommandAction writeCommandAction = new WriteCommandAction(parent.getContainingFile().getProject()) {
            @Override
            protected void run(@NotNull Result result) throws Throwable {
                if (index == ids.length - 1) {
                    finalParent.deleteChildRange(ids[index - 1].getNextSibling(), ids[index]);
                } else {
                    finalParent.deleteChildRange(ids[index], ids[index + 1].getPrevSibling());
                }

                if (exprs.length == ids.length) {
                    if (index == ids.length - 1) {
                        finalParent.deleteChildRange(exprs[index - 1].getNextSibling(), exprs[index]);
                    } else {
                        finalParent.deleteChildRange(exprs[index], exprs[index + 1].getPrevSibling());
                    }
                }
            }
        };
        writeCommandAction.execute();
    }

    private static boolean isIdWithBlank(GoLiteralIdentifier id, GoLiteralIdentifier[] ids) {
        for (GoLiteralIdentifier i : ids) {
            if (!i.isBlank() && !i.isEquivalentTo(id)) {
                return false;
            }
        }
        return true;
    }

    private static int identifierIndex(GoLiteralIdentifier[] ids, GoLiteralIdentifier id) {
        for (int i = 0; i < ids.length; i++) {
            if (ids[i].isEquivalentTo(id)) {
                return i;
            }
        }
        return -1;
    }
}
