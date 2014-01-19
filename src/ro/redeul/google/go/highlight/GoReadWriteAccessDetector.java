package ro.redeul.google.go.highlight;

import com.intellij.codeInsight.highlighting.ReadWriteAccessDetector;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.psi.declarations.GoVarSpec;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.expressions.GoExpressionList;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.expressions.primary.GoLiteralExpression;
import ro.redeul.google.go.lang.psi.statements.GoAssignmentStatement;

import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.isNodeOfType;
import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.resolveSafely;

public class GoReadWriteAccessDetector extends ReadWriteAccessDetector {
    @Override
    public boolean isReadWriteAccessible(PsiElement element) {
        if (!(element instanceof GoLiteralIdentifier)) {
            return false;
        }

        if (element.getParent() instanceof GoVarSpec) {
            return true;
        }

        PsiElement resolve = resolveSafely(element, PsiElement.class);
        return resolve instanceof GoLiteralIdentifier && resolve.getParent() instanceof GoVarSpec;

    }

    @Override
    public boolean isDeclarationWriteAccess(PsiElement element) {
        return element instanceof GoLiteralIdentifier && element.getParent() instanceof GoVarSpec;
    }

    @Override
    public Access getReferenceAccess(PsiElement referencedElement, PsiReference reference) {
        return getExpressionAccess(reference.getElement());
    }

    @Override
    public Access getExpressionAccess(PsiElement expression) {
        PsiElement parent = expression.getParent();
        if (parent instanceof GoLiteralExpression) {
            parent = parent.getParent();
        }

        if (parent instanceof GoVarSpec) {
            for (GoLiteralIdentifier id : ((GoVarSpec) parent).getIdentifiers()) {
                if (id.getTextRange().equals(expression.getTextRange())) {
                    return Access.Write;
                }
            }
        } else if (parent instanceof GoExpressionList && parent.getParent() instanceof GoAssignmentStatement) {
            GoAssignmentStatement assignment = (GoAssignmentStatement) parent.getParent();
            for (GoExpr expr : assignment.getLeftSideExpressions().getExpressions()) {
                if (expr.getTextRange().equals(expression.getTextRange())) {
                    if (assignment.getOperator() == GoAssignmentStatement.Op.Assign) {
                        return Access.Write;
                    } else {
                        return Access.ReadWrite;
                    }
                }
            }
        } else if (isNodeOfType(parent, GoElementTypes.INC_DEC_STATEMENT)) {
            return Access.ReadWrite;
        }
        return Access.Read;
    }
}
