package ro.redeul.google.go.inspection;

import java.util.List;

import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemHighlightType;
import ro.redeul.google.go.lang.psi.declarations.GoVarDeclaration;
import ro.redeul.google.go.lang.psi.expressions.GoCallOrConversionExpression;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.statements.GoShortVarDeclaration;

public class VarDeclarationInspection {
    private final InspectionResult result;
    private final GoVarDeclaration varDeclaration;

    public VarDeclarationInspection(InspectionManager manager, GoVarDeclaration varDeclaration) {
        this.result = new InspectionResult(manager);
        this.varDeclaration = varDeclaration;
    }

    public List<ProblemDescriptor> checkVar() {
        hasAssignmentCountMismatch();
        return result.getProblems();
    }

    private void hasAssignmentCountMismatch() {
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

        String msg = String.format("Assignment count mismatch: %d = %d", ids.length, exprs.length);
        result.addProblem(varDeclaration, msg, ProblemHighlightType.GENERIC_ERROR);
    }
}
