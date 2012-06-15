package ro.redeul.google.go.inspection;

import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.PsiReference;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.GoBundle;
import ro.redeul.google.go.inspection.fix.CreateFunctionFix;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.types.GoTypeName;
import ro.redeul.google.go.lang.psi.visitors.GoRecursiveElementVisitor;

import static ro.redeul.google.go.inspection.fix.CreateFunctionFix.isFunctionCallIdentifier;

public class UnresolvedSymbols extends AbstractWholeGoFileInspection {
    @Nls
    @NotNull
    @Override
    public String getDisplayName() {
        return "Highlights unresolved symbols";
    }

    @Override
    protected void doCheckFile(@NotNull GoFile file, @NotNull final InspectionResult result, boolean isOnTheFly) {

        new GoRecursiveElementVisitor() {
            @Override
            public void visitIdentifier(GoLiteralIdentifier identifier) {
                if (!identifier.isIota()) {
                    tryToResolveReference(identifier, identifier.getReference());
                }
            }

            @Override
            public void visitTypeName(GoTypeName typeName) {
                if (!typeName.isPrimitive()) {
                    tryToResolveReference(typeName, typeName.getReference());
                }
            }

            private void tryToResolveReference(PsiNamedElement element, PsiReference reference) {
                if (reference != null && reference.resolve() == null) {
                    LocalQuickFix fix = isFunctionCallIdentifier(element) ? new CreateFunctionFix(element) : null;
                    result.addProblem(element, GoBundle.message("warning.unresolved.symbol", element.getName()),
                            ProblemHighlightType.LIKE_UNKNOWN_SYMBOL, fix);
                }
            }
        }.visitElement(file);
    }
}
