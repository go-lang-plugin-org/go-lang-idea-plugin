package ro.redeul.google.go.inspection;

import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.declarations.GoVarDeclaration;
import ro.redeul.google.go.lang.psi.expressions.primary.GoCallOrConversionExpression;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.expressions.primary.GoLiteralExpression;
import ro.redeul.google.go.lang.psi.statements.GoShortVarDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionParameter;
import ro.redeul.google.go.lang.psi.visitors.GoRecursiveElementVisitor;

import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.findChildOfClass;

public class VarDeclarationInspection extends AbstractWholeGoFileInspection {

    @Override
    protected void doCheckFile(@NotNull GoFile file, @NotNull final InspectionResult result, boolean isOnTheFly) {
        new GoRecursiveElementVisitor() {
            @Override
            public void visitVarDeclaration(GoVarDeclaration varDeclaration) {
                checkVar(varDeclaration, result);
            }

            @Override
            public void visitShortVarDeclaration(GoShortVarDeclaration shortVarDeclaration) {
                checkVar(shortVarDeclaration, result);
            }
        }.visitFile(file);
    }

    public static void checkVar(GoVarDeclaration varDeclaration,
                                InspectionResult result) {
        GoLiteralIdentifier[] ids = varDeclaration.getIdentifiers();
        GoExpr[] exprs = varDeclaration.getExpressions();
        if (ids.length == exprs.length) {
            checkExpressionShouldReturnOneResult(exprs, result);
            return;
        }

        // var declaration could has no initialization expression, but short var declaration couldn't
        if (exprs.length == 0 && !(varDeclaration instanceof GoShortVarDeclaration)) {
            return;
        }

        int idCount = ids.length;
        int exprCount = exprs.length;

        if (exprs.length == 1 && exprs[0] instanceof GoCallOrConversionExpression) {
            exprCount = getFunctionCallResultCount((GoCallOrConversionExpression) exprs[0]);
            if (exprCount == UNKNOWN_COUNT || exprCount == idCount) {
                return;
            }
        }

        String msg = String.format("Assignment count mismatch: %d = %d", idCount, exprCount);
        result.addProblem(varDeclaration, msg,
                          ProblemHighlightType.GENERIC_ERROR);
    }

    private static void checkExpressionShouldReturnOneResult(GoExpr[] exprs, InspectionResult result) {
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
                String msg = String.format("Multiple-value %s() in single-value context", id.getText());
                result.addProblem(expr, msg, ProblemHighlightType.GENERIC_ERROR);
            }
        }
    }

    private static final int UNKNOWN_COUNT = -1;

    private static int getFunctionCallResultCount(GoCallOrConversionExpression call) {
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

    private static GoLiteralIdentifier getFunctionIdentifier(GoCallOrConversionExpression call) {
        GoLiteralExpression literal = findChildOfClass(call, GoLiteralExpression.class);
        if (literal == null) {
            return null;
        }

        PsiElement child = literal.getFirstChild();
        return child instanceof GoLiteralIdentifier ? (GoLiteralIdentifier) child : null;

    }
}
