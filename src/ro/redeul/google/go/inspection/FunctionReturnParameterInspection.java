package ro.redeul.google.go.inspection;

import com.intellij.codeInspection.ProblemHighlightType;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.GoBundle;
import ro.redeul.google.go.inspection.fix.CastTypeFix;
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
        private GoType[] expectedTypes;

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
            this.expectedTypes = declaration.getReturnTypes();
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

            int expectedTypeIdx = 0;



            GoFile currentFile = (GoFile) statement.getContainingFile();

            result.resetCount();
            if (expressions.length != 1) {
                for (GoExpr expression : expressions) {
                    if (expression.getType().length != 1) {
                        result.addProblem(expression, GoBundle.message("error.multiple.value.in.single.value.context", expression.getText()));
                    }
                }
            }
            if (result.getCount() != 0)
                return;

            if (expressions.length == 1) {
                GoExpr arg = expressions[0];
                GoType argTypes[] = arg.getType();
                // we have a single value as a single argument, let's see if it matches
                if ( argTypes.length > 1) {
                    // if we have to few arguments and the definition is not variadic we complain and bail
                    if (expectedTypes.length < argTypes.length) {
                        result.addProblem(arg, GoBundle.message("error.return.extra.args"), new ChangeReturnsParametersFix(statement));
                        return;
                    }

                    if ( expectedTypes.length > argTypes.length) {
                        result.addProblem(arg, GoBundle.message("error.return.enough.args"), new ChangeReturnsParametersFix(statement));
                        return;
                    }

                    // validate argument types for a multi valued expression
                    for (int i = 0; i < argTypes.length; i++) {
                        GoType argType = argTypes[i];
                        GoType paramType = expectedTypes[i];

                        if (!paramType.isAssignableFrom(argType)) {
                            result.addProblem(arg,
                                    GoBundle.message(
                                            "error.return.wrong.arg.type",
                                            arg.getText(),
                                            GoTypes.getRepresentation(argType, currentFile),
                                            GoTypes.getRepresentation(paramType, currentFile)),
                                    ProblemHighlightType.GENERIC_ERROR_OR_WARNING,
                                    new ChangeReturnsParametersFix(statement));
                            break;
                        }
                    }

                    return;
                }
            }

            int exprIdx = 0, exprCount = expressions.length;
            for (; exprIdx < exprCount; exprIdx++) {
                GoExpr expr = expressions[exprIdx];
                if (exprIdx >= expectedTypes.length) {
                    result.addProblem(expr, GoBundle.message("error.return.extra.arg"), new ChangeReturnsParametersFix(statement));
                    continue;
                }

                GoType expectedType = expectedTypes[exprIdx];
                GoType argumentType = GoTypes.get(expr.getType());

                if (!expectedType.isAssignableFrom(argumentType)) {
                    result.addProblem(
                            expr,
                            GoBundle.message(
                                    "warn.function.return.type.mismatch",
                                    expr.getText(),
                                    GoTypes.getRepresentation(argumentType, currentFile),
                                    GoTypes.getRepresentation(expectedType, currentFile)),
                            ProblemHighlightType.GENERIC_ERROR_OR_WARNING,
                            new CastTypeFix(expr, expectedType),
                            new ChangeReturnsParametersFix(statement));
                }
            }

            if ( exprIdx < expectedTypes.length )
                result.addProblem(statement, GoBundle.message("error.return.enough.args"), new ChangeReturnsParametersFix(statement));
        }
    }
}
