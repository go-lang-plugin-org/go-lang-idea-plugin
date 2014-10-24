package ro.redeul.google.go.lang.psi.resolve;

import com.intellij.psi.ResolveState;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitorWithData;

public abstract class ReferenceSolvingVisitor extends GoElementVisitorWithData<ResolveState> {

    private ReferenceSolver solver;
    private Reference reference;

    public ReferenceSolvingVisitor(ReferenceSolver solver, Reference reference) {
        this.solver = solver;
        this.reference = reference;
    }

    protected void checkIdentifiers(String name, GoLiteralIdentifier... identifiers) {

        for (GoLiteralIdentifier identifier : identifiers) {

            String identifierName = identifier.getUnqualifiedName();

            if ( !reference.canSee(identifier, identifierName))
                continue;

            if (matchNames(name, identifierName))
                solver.addTarget(identifier);
        }
    }

    protected boolean matchNames(String name, @NotNull String potentialTarget) {
        return solver.collectingVariants() || potentialTarget.equals(name);
    }
}

