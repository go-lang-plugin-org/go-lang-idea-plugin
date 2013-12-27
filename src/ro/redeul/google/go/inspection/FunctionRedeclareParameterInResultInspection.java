package ro.redeul.google.go.inspection;

import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.GoBundle;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralFunction;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionParameter;
import ro.redeul.google.go.lang.psi.toplevel.GoMethodDeclaration;
import ro.redeul.google.go.lang.psi.visitors.GoRecursiveElementVisitor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FunctionRedeclareParameterInResultInspection extends AbstractWholeGoFileInspection
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
        hasRedeclaredParameterInResultList(result, function);
    }

    private static void hasRedeclaredParameterInResultList(InspectionResult result, GoFunctionDeclaration function) {
        Set<String> parameters = new HashSet<String>(getParameterNames(function.getParameters()));

        for (GoFunctionParameter fp : function.getResults()) {
            for (GoLiteralIdentifier id : fp.getIdentifiers()) {
                String text = id.getText();
                if (!id.isBlank() && parameters.contains(text)) {
                    result.addProblem(id, GoBundle.message("error.redeclared.in.block", text));
                }
            }
        }
    }

    private static List<String> getParameterNames(GoFunctionParameter[] parameters) {
        List<String> parameterNames = new ArrayList<String>();
        for (GoFunctionParameter fp : parameters) {
            for (GoLiteralIdentifier id : fp.getIdentifiers()) {
                if (!id.isBlank()) {
                    parameterNames.add(id.getText());
                }
            }
        }
        return parameterNames;
    }

}
