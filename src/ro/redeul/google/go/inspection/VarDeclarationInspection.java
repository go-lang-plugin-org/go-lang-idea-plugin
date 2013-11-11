package ro.redeul.google.go.inspection;

import com.intellij.codeInspection.ProblemHighlightType;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.declarations.GoVarDeclaration;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.statements.GoShortVarDeclaration;
import ro.redeul.google.go.lang.psi.visitors.GoRecursiveElementVisitor;

import static ro.redeul.google.go.inspection.InspectionUtil.*;

public class VarDeclarationInspection extends AbstractWholeGoFileInspection {

    @Override
    protected void doCheckFile(@NotNull GoFile file, @NotNull final InspectionResult result) {
        new GoRecursiveElementVisitor() {
            @Override
            public void visitVarDeclaration(GoVarDeclaration declaration) {
                checkVar(declaration, result);
            }

            @Override
            public void visitShortVarDeclaration(GoShortVarDeclaration declaration) {
                checkVar(declaration, result);
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

        if (exprs.length == 1) {
            exprCount = getExpressionResultCount(exprs[0]);
            if (exprCount == UNKNOWN_COUNT || exprCount == idCount) {
                return;
            }
        }

        String msg = String.format("Assignment count mismatch: %d = %d", idCount, exprCount);
        result.addProblem(varDeclaration, msg,
                          ProblemHighlightType.GENERIC_ERROR);
    }
}
