package ro.redeul.google.go.inspection;

import com.intellij.codeInspection.ProblemHighlightType;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.declarations.GoConstSpec;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.visitors.GoRecursiveElementVisitor;

import static ro.redeul.google.go.GoBundle.message;

public class ConstantExpressionsInConstDeclarationsInspection
        extends AbstractWholeGoFileInspection {

    @Override
    protected void doCheckFile(@NotNull GoFile file, @NotNull final InspectionResult result) {
        new GoRecursiveElementVisitor() {
            @Override
            public void visitConstDeclaration(GoConstSpec declaration) {
                checkConstDeclaration(declaration, result);
            }
        }.visitFile(file);
    }

    private void checkConstDeclaration(GoConstSpec declaration, InspectionResult result) {
        GoExpr[] expressions = declaration.getExpressions();
        for (GoExpr expression : expressions) {
            if (!expression.isConstantExpression()) {
                result.addProblem(expression,
                        message("error.non.constant.expression"),
                        ProblemHighlightType.WEAK_WARNING);

            }
        }
    }
}
