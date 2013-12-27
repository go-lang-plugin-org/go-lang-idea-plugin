package ro.redeul.google.go.inspection;

import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.GoBundle;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralFunction;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionParameter;
import ro.redeul.google.go.lang.psi.toplevel.GoMethodDeclaration;
import ro.redeul.google.go.lang.psi.visitors.GoRecursiveElementVisitor;

public class FunctionVaraidicParameterInspection extends AbstractWholeGoFileInspection
{
    @Override
    protected void doCheckFile(@NotNull GoFile file, @NotNull final InspectionResult result) {

        new GoRecursiveElementVisitor() {
            @Override
            public void visitFunctionDeclaration(GoFunctionDeclaration declaration) {
                checkFunction(result, declaration);
            }

            @Override
            public void visitMethodDeclaration(GoMethodDeclaration declaration) {
                checkFunction(result, declaration);
            }

            @Override
            public void visitFunctionLiteral(GoLiteralFunction literal) {
                checkFunction(result, literal);
            }
        }.visitFile(file);
    }

    public static void checkFunction(InspectionResult result, GoFunctionDeclaration function) {
        hasVariadicProblems(result, function);
    }

    private static void hasVariadicProblems(InspectionResult result, GoFunctionDeclaration function) {
        // cannot use variadic in output argument list
        for (GoFunctionParameter parameter : function.getResults()) {
            if (parameter.isVariadic()) {
                result.addProblem(parameter, GoBundle.message("error.output.variadic"));
            }
        }

        GoFunctionParameter[] parameters = function.getParameters();
        if (parameters.length == 0) {
            return;
        }

        // only last argument could be variadic
        for (int i = 0; i < parameters.length - 1; i++) {
            GoFunctionParameter parameter = parameters[i];
            if (parameter.isVariadic()) {
                result.addProblem(parameter, GoBundle.message("error.variadic.not.the.last"));
            }
        }
    }

}
