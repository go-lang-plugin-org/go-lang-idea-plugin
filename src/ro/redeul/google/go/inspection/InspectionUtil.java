package ro.redeul.google.go.inspection;

import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import ro.redeul.google.go.GoBundle;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.expressions.primary.GoCallOrConversionExpression;
import ro.redeul.google.go.lang.psi.expressions.primary.GoLiteralExpression;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionParameter;

import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.findChildOfClass;

public class InspectionUtil {
    public static TextRange getProblemRange(ProblemDescriptor pd) {
        int start = pd.getStartElement().getTextOffset();
        int end = pd.getEndElement().getTextOffset() + pd.getEndElement().getTextLength();
        return new TextRange(start, end);
    }

    public static final int UNKNOWN_COUNT = -1;

    public static int getFunctionCallResultCount(GoCallOrConversionExpression call) {
        GoLiteralIdentifier id = getFunctionIdentifier(call);
        if (id == null) {
            return UNKNOWN_COUNT;
        }

        PsiElement resolve = id.resolve();
        if (!(resolve instanceof GoFunctionDeclaration)) {
            return UNKNOWN_COUNT;
        }

        int count = 0;
        for (GoFunctionParameter result : ((GoFunctionDeclaration) resolve).getResults()) {
            count += Math.max(result.getIdentifiers().length, 1);
        }
        return count;
    }

    public static GoLiteralIdentifier getFunctionIdentifier(GoCallOrConversionExpression call) {
        GoLiteralExpression literal = findChildOfClass(call, GoLiteralExpression.class);
        if (literal == null) {
            return null;
        }

        PsiElement child = literal.getFirstChild();
        return child instanceof GoLiteralIdentifier ? (GoLiteralIdentifier) child : null;

    }

    public static void checkExpressionShouldReturnOneResult(GoExpr[] exprs, InspectionResult result) {
        for (GoExpr expr : exprs) {
            if (!(expr instanceof GoCallOrConversionExpression)) {
                continue;
            }

            GoCallOrConversionExpression call = (GoCallOrConversionExpression) expr;
            GoLiteralIdentifier id = getFunctionIdentifier(call);
            if (id == null) {
                continue;
            }

            int count = getFunctionCallResultCount(call);
            if (count != UNKNOWN_COUNT && count != 1) {
                String msg = GoBundle.message("error.multiple.value.in.single.value.context", id.getText());
                result.addProblem(expr, msg, ProblemHighlightType.GENERIC_ERROR);
            }
        }
    }
}
