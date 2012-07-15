package ro.redeul.google.go.inspection;

import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.inspection.fix.CreateFunctionFix;
import ro.redeul.google.go.inspection.fix.CreateGlobalVariableFix;
import ro.redeul.google.go.inspection.fix.CreateLocalVariableFix;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.declarations.GoVarDeclarations;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.expressions.primary.GoSelectorExpression;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionDeclaration;
import ro.redeul.google.go.lang.psi.types.GoTypeName;
import ro.redeul.google.go.lang.psi.visitors.GoRecursiveElementVisitor;
import static ro.redeul.google.go.GoBundle.message;
import static ro.redeul.google.go.inspection.fix.CreateFunctionFix.isExternalFunctionNameIdentifier;
import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.findParentOfType;
import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.resolveSafely;

public class UnresolvedSymbols extends AbstractWholeGoFileInspection {
    @Nls
    @NotNull
    @Override
    public String getDisplayName() {
        return "Highlights unresolved symbols";
    }

    @Override
    protected void doCheckFile(@NotNull GoFile file,
                               @NotNull final InspectionResult result,
                               boolean isOnTheFly) {

        new GoRecursiveElementVisitor() {
            @Override
            public void visitLiteralIdentifier(GoLiteralIdentifier identifier) {
                if (!identifier.isIota() && !identifier.isBlank()) {
                    tryToResolveReference(identifier);
                }
            }

            @Override
            public void visitTypeName(GoTypeName typeName) {
                if (!typeName.isPrimitive()) {
                    tryToResolveReference(typeName);
                }
            }

            private void tryToResolveReference(PsiNamedElement element) {
                if (element.getReferences().length > 0 &&
                        resolveSafely(element, PsiElement.class) == null) {

                    LocalQuickFix[] fixes;
                    if (isExternalFunctionNameIdentifier(element)) {
                        fixes = new LocalQuickFix[]{new CreateFunctionFix(element)};
                    } else if (isLocalVariableIdentifier(element)) {
                        fixes = new LocalQuickFix[]{new CreateLocalVariableFix(element),
                                new CreateGlobalVariableFix(element)};
                    } else if (isGlobalVariableIdentifier(element)) {
                        fixes = new LocalQuickFix[]{new CreateGlobalVariableFix(element)};
                    } else {
                        fixes = LocalQuickFix.EMPTY_ARRAY;
                    }

                    result.addProblem(
                        element,
                        message("warning.unresolved.symbol", element.getName()),
                        ProblemHighlightType.LIKE_UNKNOWN_SYMBOL, fixes);
                }
            }
        }.visitElement(file);
    }

    private static boolean isGlobalVariableIdentifier(PsiNamedElement element) {
        return element instanceof GoLiteralIdentifier &&
               findParentOfType(element, GoSelectorExpression.class) == null &&
               findParentOfType(element, GoFunctionDeclaration.class) == null &&
               findParentOfType(element, GoVarDeclarations.class) != null;
    }

    private static boolean isLocalVariableIdentifier(PsiNamedElement element) {
        return element instanceof GoLiteralIdentifier &&
               findParentOfType(element, GoSelectorExpression.class) == null &&
               findParentOfType(element, GoFunctionDeclaration.class) != null;
    }
}
