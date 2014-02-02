package ro.redeul.google.go.inspection;

import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.GoBundle;
import ro.redeul.google.go.inspection.fix.ChangeReturnsParametersFix;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralFunction;
import ro.redeul.google.go.lang.psi.statements.GoReturnStatement;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionParameter;
import ro.redeul.google.go.lang.psi.toplevel.GoMethodDeclaration;
import ro.redeul.google.go.lang.psi.visitors.GoRecursiveElementVisitor;
import ro.redeul.google.go.util.GoTypeInspectUtil;

import static ro.redeul.google.go.inspection.InspectionUtil.*;

public class FunctionReturnParameterInspection extends AbstractWholeGoFileInspection {
    @Override
    protected void doCheckFile(@NotNull GoFile file, @NotNull final InspectionResult result) {

        new GoRecursiveElementVisitor() {
            @Override
            public void visitFunctionDeclaration(GoFunctionDeclaration declaration) {
                visitElement(declaration);
                checkFunction(result, declaration);
            }

            @Override
            public void visitMethodDeclaration(GoMethodDeclaration declaration) {
                visitElement(declaration);
                checkFunction(result, declaration);
            }

            @Override
            public void visitFunctionLiteral(GoLiteralFunction literal) {
                visitElement(literal);
                checkFunction(result, literal);
            }
        }.visitFile(file);
    }

    public static void checkFunction(InspectionResult result, GoFunctionDeclaration function) {
        hasReturnParameterCountMismatch(result, function);
    }

    private static void hasReturnParameterCountMismatch(InspectionResult result, GoFunctionDeclaration function) {
        new ReturnVisitor(result, function).visitFunctionDeclaration(function);
    }

    /**
     * Recursively look for return statement, and compare its expression
     * list with function's result list
     */
    private static class ReturnVisitor extends GoRecursiveElementVisitor {
        private final InspectionResult result;
        int expectedResCount;
        boolean hasNamedReturns;

        public ReturnVisitor(InspectionResult result, GoFunctionDeclaration declaration) {
            this.result = result;
            this.expectedResCount = 0;

            hasNamedReturns = false;
            for (GoFunctionParameter resParam : declaration.getResults()) {
                expectedResCount += Math.max(resParam.getIdentifiers().length, 1);
                hasNamedReturns |= resParam.getIdentifiers().length > 0;
            }
        }

        @Override
        public void visitFunctionLiteral(GoLiteralFunction literal) {
            // stop the recursion here
        }

        @Override
        public void visitReturnStatement(GoReturnStatement statement) {
            GoExpr[] expressions = statement.getExpressions();
            int returnCount = expressions.length;
            if (returnCount == 1) {
                returnCount = getExpressionResultCount(expressions[0]);
            } else {
                checkExpressionShouldReturnOneResult(expressions, result);
            }

            // when a method specifies named return parameters it's ok to have
            // an empty return statement.
            if (returnCount == UNKNOWN_COUNT ||
                    returnCount == 0 && hasNamedReturns) {
                return;
            }

            if (expectedResCount < returnCount) {
                result.addProblem(statement, GoBundle.message("error.too.many.arguments.to.return"),
                        new ChangeReturnsParametersFix(statement));
            } else if (expectedResCount > returnCount) {
                result.addProblem(statement, GoBundle.message("error.not.enough.arguments.to.return"),
                        new ChangeReturnsParametersFix(statement));
            } else {
                GoTypeInspectUtil.checkFunctionTypeReturns(statement, result);
            }


        }
    }
}
