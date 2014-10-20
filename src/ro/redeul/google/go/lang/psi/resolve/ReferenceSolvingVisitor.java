package ro.redeul.google.go.lang.psi.resolve;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.ResolveState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.lang.psi.GoPsiElement;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.processors.GoNamesUtil;
import ro.redeul.google.go.lang.psi.processors.ResolveStates;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitorWithData;
import ro.redeul.google.go.lang.stubs.GoNamesCache;

public abstract class ReferenceSolvingVisitor extends GoElementVisitorWithData<ResolveState> {

    private ReferenceSolver solver;
    private Reference reference;

    public ReferenceSolvingVisitor(ReferenceSolver solver, Reference reference) {
        this.solver = solver;
        this.reference = reference;
    }

    @Nullable
    protected String referenceName() {
        PsiElement element = reference.getElement();

        return (element != null && element instanceof PsiNamedElement)
                    ? ((PsiNamedElement) element).getName()
                    : element != null
                        ? element.getText() : null;
    }

    protected void checkIdentifiers(String name, GoLiteralIdentifier... identifiers) {
        for (GoLiteralIdentifier identifier : identifiers) {

            String identifierName = identifier.getUnqualifiedName();

            if (ResolveStates.get(getData(), ResolveStates.Key.JustExports) && !GoNamesUtil.isExported(identifierName))
                continue;

            if (matchNames(name, identifierName))
                solver.addTarget(identifier);
        }
    }

    protected boolean matchNames(String name, @NotNull String potentialTarget) {
        return solver.collectingVariants() || potentialTarget.equals(name);
    }
}

