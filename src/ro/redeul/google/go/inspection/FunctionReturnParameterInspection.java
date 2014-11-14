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
import ro.redeul.google.go.lang.psi.typing.GoType;
import ro.redeul.google.go.lang.psi.typing.GoTypes;
import ro.redeul.google.go.lang.psi.visitors.GoRecursiveElementVisitor;

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
        function.accept(new ReturnVisitor(result));
    }

    /**
     * Recursively look for return statement, and compare its expression
     * list with function's result list
     */
    private static class ReturnVisitor extends GoRecursiveElementVisitor {

        private final InspectionResult result;
        private boolean hasNamedReturns;
        private GoType[] expectedReturnTypes;

        public ReturnVisitor(InspectionResult result) {
            this.result = result;
        }

        private boolean checkNamedReturns(GoFunctionDeclaration declaration) {
            for (GoFunctionParameter returnParameter : declaration.getResults()) {
                if (returnParameter.getIdentifiers().length > 0)
                    return true;
            }

            return false;
        }

        @Override
        public void visitFunctionDeclaration(GoFunctionDeclaration declaration) {
            this.expectedReturnTypes = declaration.getReturnTypes();
            this.hasNamedReturns = checkNamedReturns(declaration);

            visitElement(declaration);
        }

        @Override
        public void visitMethodDeclaration(GoMethodDeclaration declaration) {
            super.visitMethodDeclaration(declaration);
        }

        @Override
        public void visitFunctionLiteral(GoLiteralFunction literal) {
            // stop the recursion here
        }

        @Override
        public void visitReturnStatement(GoReturnStatement statement) {
            GoExpr[] expressions = statement.getExpressions();

            // if a method has named Returns we can have an empty return statement
            if (expressions.length == 0 && hasNamedReturns)
                return;

            int returnTypeIndex = 0;

            GoFile currentFile = (GoFile) statement.getContainingFile();
            // match the expression types with the expected return types.
            for (GoExpr expression : expressions) {
                GoType[] expressionTypes = expression.getType();
                if (expressionTypes.length > 1 && expressions.length > 1) {
                    result.addProblem(
                            expression,
                            GoBundle.message("error.multiple.value.in.single.value.context", expression.getText())
                    );

                    returnTypeIndex++;
                    continue;
                }

                for (GoType expressionType : expressionTypes) {
                    if (returnTypeIndex >= expectedReturnTypes.length) {
                        result.addProblem(
                                statement,
                                GoBundle.message("error.too.many.arguments.to.return"),
                                new ChangeReturnsParametersFix(statement));
                        return;
                    }

                    if (!expectedReturnTypes[returnTypeIndex].isAssignableFrom(expressionType)) {
                        result.addProblem(expression,
                                GoBundle.message("warn.function.return.type.mismatch",
                                        expression.getText(),
                                        GoTypes.getRepresentation(expressionType, currentFile),
                                        GoTypes.getRepresentation(expectedReturnTypes[returnTypeIndex], currentFile)));
                    }

                    returnTypeIndex++;
                }
            }

            if (returnTypeIndex < expectedReturnTypes.length)
                result.addProblem(statement,
                        GoBundle.message("error.not.enough.arguments.to.return"),
                        new ChangeReturnsParametersFix(statement));
        }
    }
}
