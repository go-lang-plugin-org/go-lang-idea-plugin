package ro.redeul.google.go.inspection;

import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import ro.redeul.google.go.GoBundle;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.psi.GoPsiElement;
import ro.redeul.google.go.lang.psi.declarations.GoVarDeclaration;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.expressions.GoUnaryExpression;
import ro.redeul.google.go.lang.psi.expressions.binary.GoBinaryExpression;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.expressions.primary.*;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionParameter;

import static ro.redeul.google.go.lang.psi.utils.GoExpressionUtils.getCallFunctionIdentifier;
import static ro.redeul.google.go.lang.psi.utils.GoExpressionUtils.resolveToFunctionDeclaration;
import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.isNodeOfType;

public class InspectionUtil {
    public static TextRange getProblemRange(ProblemDescriptor pd) {
        int start = pd.getStartElement().getTextOffset();
        int end = pd.getEndElement().getTextOffset() + pd.getEndElement()
                .getTextLength();
        return new TextRange(start, end);
    }

    public static final int UNKNOWN_COUNT = -1;
    public static final int VARIADIC_COUNT = -2;

    public static int getExpressionResultCount(GoExpr call) {
        if (call instanceof GoLiteralExpression
                || call instanceof GoBinaryExpression
                || (call instanceof GoUnaryExpression && ((GoUnaryExpression) call).getUnaryOp() != GoUnaryExpression.Op.Channel)
                || call instanceof GoParenthesisedExpression
                || call instanceof GoSelectorExpression
                ) {
            return 1;
        } else if (call instanceof GoTypeAssertionExpression) {
            return getTypeAssertionResultCount(
                    (GoTypeAssertionExpression) call);
        } else if (call instanceof GoCallOrConvExpression) {
            return getFunctionResultCount((GoCallOrConvExpression) call);
        }

        return UNKNOWN_COUNT;
    }

    private static int getTypeAssertionResultCount(GoTypeAssertionExpression expression) {
        PsiElement parent = expression.getParent();
        if (isNodeOfType(parent, GoElementTypes.ASSIGN_STATEMENT)) {
            // TODO: get expressions and identifiers of assign statement
            return UNKNOWN_COUNT;
        }

        if (!(parent instanceof GoVarDeclaration)) {
            return 1;
        }

        GoLiteralIdentifier[] identifiers = ((GoVarDeclaration) parent).getIdentifiers();
        GoExpr[] expressions = ((GoVarDeclaration) parent).getExpressions();
        // if the type assertion is the only expression, and there are two variables.
        // The result of the type assertion is a pair of values with types (T, bool)
        if (identifiers.length == 2 && expressions.length == 1) {
            return 2;
        }

        return 1;
    }

    private static int getFunctionResultCount(GoCallOrConvExpression call) {
        GoFunctionDeclaration function = resolveToFunctionDeclaration(call);
        return function == null ? UNKNOWN_COUNT : getFunctionResultCount(function);
    }

    public static int getFunctionResultCount(GoFunctionDeclaration function) {
        int count = 0;
        for (GoFunctionParameter p : function.getResults()) {
            count += Math.max(p.getIdentifiers().length, 1);
        }
        return count;
    }


    public static int getFunctionParameterCount(GoCallOrConvExpression call) {
        GoFunctionDeclaration function = resolveToFunctionDeclaration(call);
        if (function == null) {
            return UNKNOWN_COUNT;
        }

        int count = 0;
        for (GoFunctionParameter p : function.getParameters()) {
            count += Math.max(p.getIdentifiers().length, 1);
            if (p.isVariadic()) {
                return VARIADIC_COUNT;
            }
        }
        return count;
    }

    public static void checkExpressionShouldReturnOneResult(GoExpr[] exprs, InspectionResult result) {
        for (GoExpr expr : exprs) {
            int count = getExpressionResultCount(expr);
            if (count != UNKNOWN_COUNT && count != 1) {
                String text = expr.getText();
                if (expr instanceof GoCallOrConvExpression) {
                    GoPsiElement id = getCallFunctionIdentifier((GoCallOrConvExpression) expr);
                    if (id == null) {
                        continue;
                    }
                    text = id.getText();
                }

                String msg = GoBundle.message(
                        "error.multiple.value.in.single.value.context", text);
                result.addProblem(expr, msg,
                        ProblemHighlightType.GENERIC_ERROR);
            }
        }
    }
}
