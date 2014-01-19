package ro.redeul.google.go.inspection;

import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.GoBundle;
import ro.redeul.google.go.inspection.fix.DeleteStmtFix;
import ro.redeul.google.go.inspection.fix.constDeclaration.AddMissingConstFix;
import ro.redeul.google.go.inspection.fix.constDeclaration.AddMissingExpressionFix;
import ro.redeul.google.go.inspection.fix.constDeclaration.RemoveRedundantConstFix;
import ro.redeul.google.go.inspection.fix.constDeclaration.RemoveRedundantExpressionFix;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.declarations.GoConstSpec;
import ro.redeul.google.go.lang.psi.declarations.GoConstDeclarations;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.visitors.GoRecursiveElementVisitor;

public class ConstDeclarationInspection extends AbstractWholeGoFileInspection {

    @Override
    protected void doCheckFile(@NotNull GoFile file,
                               @NotNull final InspectionResult result) {

        new GoRecursiveElementVisitor() {
            @Override
            public void visitConstDeclarations(GoConstDeclarations declarations) {
                checkConstDeclarations(declarations, result);
                visitElement(declarations);
            }

            @Override
            public void visitConstDeclaration(GoConstSpec declaration) {
                checkConstDeclaration(declaration, result);
            }
        }.visitFile(file);
    }

    public static void checkConstDeclarations(GoConstDeclarations constDecls,
                                              InspectionResult result) {
        if (isMissingFirstExpressionDeclaration(constDecls)) {
            result.addProblem(constDecls.getDeclarations()[0],
                              GoBundle.message(
                                  "error.missing.first.const.expression"),
                              ProblemHighlightType.ERROR,
                              new AddMissingExpressionFix(),
                              new DeleteStmtFix()
                    );
        }
    }

    public static void checkConstDeclaration(GoConstSpec declaration,
                                             InspectionResult results) {

        if (isNoExpressionAndConstCountMismatch(declaration)) {
            results.addProblem(declaration,
                    GoBundle.message(
                            "error.no.expression.const.count.mismatch.in.const.declaration"),
                    ProblemHighlightType.GENERIC_ERROR
                    );
        } else if (isMissingExpressionInConst(declaration)) {
            results.addProblem(declaration,
                    GoBundle.message(
                            "error.missing.expr.for.const.declaration"),
                    ProblemHighlightType.GENERIC_ERROR,
                    new AddMissingExpressionFix(),
                    new RemoveRedundantConstFix()
            );
        } else if (isExtraExpressionInConst(declaration)) {
            results.addProblem(declaration,
                    GoBundle.message(
                            "error.extra.expr.in.const.declaration"),
                    ProblemHighlightType.GENERIC_ERROR,
                    new AddMissingConstFix(),
                    new RemoveRedundantExpressionFix()
            );
        }
    }

    // According to spec:
    // Within a parenthesized const declaration list the expression list may be omitted from any but the first declaration
    private static boolean isMissingFirstExpressionDeclaration(
        GoConstDeclarations constDeclarations)
    {
        GoConstSpec[] declarations = constDeclarations.getDeclarations();
        return declarations.length != 0 && declarations[0].getExpressions().length == 0;
    }

    private static boolean isMissingExpressionInConst(GoConstSpec declaration) {
        GoLiteralIdentifier[] ids = declaration.getIdentifiers();
        GoExpr[] exprs = declaration.getExpressions();

        return
            declaration.hasInitializers() &&
                ids.length > exprs.length;
    }

    // According to spec:
    // Omitting the list of expressions is therefore equivalent to repeating the previous list.
    // The number of identifiers must be equal to the number of expressions in the previous list
    private static boolean isNoExpressionAndConstCountMismatch(GoConstSpec declaration) {
        if (declaration.hasInitializers()) {
            return false;
        }

        PsiElement element = declaration;
        while ((element = element.getPrevSibling()) != null) {
            if (element instanceof GoConstSpec) {
                GoConstSpec preConst = (GoConstSpec) element;
                if (preConst.hasInitializers()) {
                    return declaration.getIdentifiers().length != preConst.getIdentifiers().length;
                }
            }
        }
        return false;
    }

    private static boolean isExtraExpressionInConst(GoConstSpec declaration) {
        GoLiteralIdentifier[] ids = declaration.getIdentifiers();
        GoExpr[] exprs = declaration.getExpressions();

        return
            declaration.hasInitializers() &&
                ids.length < exprs.length;
    }
}