package ro.redeul.google.go.inspection;

import com.intellij.codeInspection.ProblemHighlightType;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.GoBundle;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.declarations.GoConstDeclaration;
import ro.redeul.google.go.lang.psi.declarations.GoConstDeclarations;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.visitors.GoRecursiveElementVisitor;

public class ConstDeclarationInspection extends AbstractWholeGoFileInspection {

    @Override
    protected void doCheckFile(@NotNull GoFile file,
                               @NotNull final InspectionResult result,
                               boolean isOnTheFly) {

        new GoRecursiveElementVisitor() {
            @Override
            public void visitConstDeclarations(GoConstDeclarations decls) {
                checkConstDeclarations(decls, result);
                visitElement(decls);
            }

            @Override
            public void visitConstDeclaration(GoConstDeclaration declaration) {
                checkConstDeclaration(declaration, result);
            }
        }.visitFile(file);
    }

    public static void checkConstDeclarations(GoConstDeclarations constDecls,
                                              InspectionResult result) {
        if (isMissingFirstExpressionDeclaration(constDecls)) {
            result.addProblem(constDecls.getDeclarations()[0],
                              GoBundle.message(
                                  "error.missing.first.const.expresson"),
                              ProblemHighlightType.ERROR);
        }
    }

    public static void checkConstDeclaration(GoConstDeclaration declaration,
                                             InspectionResult results) {

        if (isMissingExpressionInConst(declaration)) {
            results.addProblem(declaration,
                               GoBundle.message(
                                   "error.missing.expr.for.const.declaration"),
                               ProblemHighlightType.GENERIC_ERROR);
        } else if (isExtraExpressionInConst(declaration)) {
            results.addProblem(declaration,
                               GoBundle.message(
                                   "error.extra.expr.in.const.declaration"),
                               ProblemHighlightType.GENERIC_ERROR);
        }
    }

    private static boolean isMissingFirstExpressionDeclaration(
        GoConstDeclarations constDeclarations)
    {
        GoConstDeclaration[] declarations = constDeclarations.getDeclarations();
        return declarations.length != 0 && declarations[0].getExpressions().length == 0;
    }

    private static boolean isMissingExpressionInConst(GoConstDeclaration declaration) {
        GoLiteralIdentifier[] ids = declaration.getIdentifiers();
        GoExpr[] exprs = declaration.getExpressions();

        return
            declaration.hasInitializers() &&
                ids.length > exprs.length;
    }

    private static boolean isExtraExpressionInConst(GoConstDeclaration declaration) {
        GoLiteralIdentifier[] ids = declaration.getIdentifiers();
        GoExpr[] exprs = declaration.getExpressions();

        return
            declaration.hasInitializers() &&
                ids.length < exprs.length;
    }
}