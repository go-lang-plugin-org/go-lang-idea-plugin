package ro.redeul.google.go.inspection;

import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.openapi.util.TextRange;
import com.intellij.patterns.ElementPattern;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import ro.redeul.google.go.GoBundle;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.psi.declarations.GoVarDeclaration;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.expressions.primary.GoCallOrConvExpression;
import ro.redeul.google.go.lang.psi.expressions.primary.GoLiteralExpression;
import ro.redeul.google.go.lang.psi.expressions.primary.GoTypeAssertionExpression;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionParameter;
import static com.intellij.patterns.PsiJavaPatterns.psiElement;
import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.findChildOfClass;
import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.isNodeOfType;

public class InspectionUtil {

    static ElementPattern functionDeclarationPattern =
        psiElement(GoLiteralIdentifier.class)
            .withParent(
                psiElement(GoFunctionDeclaration.class));

    public static TextRange getProblemRange(ProblemDescriptor pd) {
        int start = pd.getStartElement().getTextOffset();
        int end = pd.getEndElement().getTextOffset() + pd.getEndElement()
                                                         .getTextLength();
        return new TextRange(start, end);
    }

    public static final int UNKNOWN_COUNT = -1;

    public static int getExpressionResultCount(GoExpr call) {
        if (call instanceof GoLiteralExpression) {
            return 1;
        } else if (call instanceof GoTypeAssertionExpression) {
            return getTypeAssertionResultCount(
                (GoTypeAssertionExpression) call);
        } else if (call instanceof GoCallOrConvExpression) {
            return getFunctionResultCount((GoCallOrConvExpression) call);
        }

        return UNKNOWN_COUNT;
    }

    public static int getTypeAssertionResultCount(GoTypeAssertionExpression expression) {
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

    public static int getFunctionResultCount(GoCallOrConvExpression call) {
        GoLiteralIdentifier id = getFunctionIdentifier(call);
        if (id == null) {
            return UNKNOWN_COUNT;
        }

        PsiElement definition = resolveIdentifier(id);

        if (functionDeclarationPattern.accepts(definition)) {
            return getFunctionResultCount(
                (GoFunctionDeclaration) definition.getParent());
        }

        return UNKNOWN_COUNT;
    }

    public static int getFunctionResultCount(GoFunctionDeclaration function) {
        int count = 0;
        for (GoFunctionParameter p : function.getResults()) {
            count += Math.max(p.getIdentifiers().length, 1);
        }
        return count;
    }


    public static PsiElement resolveIdentifier(GoLiteralIdentifier id) {
        PsiReference reference = id.getReference();
        return reference == null ? null : reference.resolve();
    }

    public static int getFunctionParameterCount(GoCallOrConvExpression call) {
        GoLiteralIdentifier id = getFunctionIdentifier(call);
        if (id == null) {
            return UNKNOWN_COUNT;
        }

        PsiElement definition = resolveIdentifier(id);

        if (functionDeclarationPattern.accepts(definition)) {
            GoFunctionDeclaration function = (GoFunctionDeclaration) definition.getParent();

            int count = 0;
            for (GoFunctionParameter p : function.getParameters()) {
                count += Math.max(p.getIdentifiers().length, 1);
                if (p.isVariadic()) {
                    return UNKNOWN_COUNT;
                }
            }
            return count;

        }

        return UNKNOWN_COUNT;
    }

    public static GoLiteralIdentifier getFunctionIdentifier(GoCallOrConvExpression call) {
        GoLiteralExpression literal = findChildOfClass(call,
                                                       GoLiteralExpression.class);
        if (literal == null) {
            return null;
        }

        PsiElement child = literal.getFirstChild();
        return child instanceof GoLiteralIdentifier ? (GoLiteralIdentifier) child : null;

    }

    public static void checkExpressionShouldReturnOneResult(GoExpr[] exprs, InspectionResult result) {
        for (GoExpr expr : exprs) {
            int count = getExpressionResultCount(expr);
            if (count != UNKNOWN_COUNT && count != 1) {
                String text = expr.getText();
                if (expr instanceof GoCallOrConvExpression) {
                    GoLiteralIdentifier id = getFunctionIdentifier(
                        (GoCallOrConvExpression) expr);
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
