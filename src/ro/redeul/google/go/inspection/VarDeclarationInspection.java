package ro.redeul.google.go.inspection;

import com.intellij.codeInspection.ProblemHighlightType;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.declarations.GoVarDeclaration;
import ro.redeul.google.go.lang.psi.expressions.GoCallOrConversionExpression;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.statements.GoShortVarDeclaration;
import ro.redeul.google.go.lang.psi.visitors.GoRecursiveElementVisitor;

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
            return;
        }

        // var declaration could has no initialization expression, but short var declaration couldn't
        if (exprs.length == 0 && !(varDeclaration instanceof GoShortVarDeclaration)) {
            return;
        }

        if (exprs.length == 1 && exprs[0] instanceof GoCallOrConversionExpression) {
            // TODO: check expression return count
            return;
        }

        String msg = String.format("Assignment count mismatch: %d = %d",
                                   ids.length, exprs.length);
        result.addProblem(varDeclaration, msg,
                          ProblemHighlightType.GENERIC_ERROR);
    }
}
