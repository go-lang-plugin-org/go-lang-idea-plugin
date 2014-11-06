package ro.redeul.google.go.lang.psi.resolve.refs;

import com.intellij.patterns.ElementPattern;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.expressions.primary.GoLiteralExpression;
import ro.redeul.google.go.lang.psi.processors.ResolveStates;
import ro.redeul.google.go.lang.psi.resolve.ReferenceWithSolver;
import ro.redeul.google.go.lang.psi.utils.GoPsiScopesUtil;

import static com.intellij.patterns.PlatformPatterns.psiElement;

public class VarOrConstReference
        extends ReferenceWithSolver<GoLiteralIdentifier, VarOrConstSolver, VarOrConstReference> {

    public static final ElementPattern<GoLiteralIdentifier> MATCHER =
            psiElement(GoLiteralIdentifier.class)
                    .withParent(
                            psiElement(GoLiteralExpression.class)
                    );

    public VarOrConstReference(GoLiteralIdentifier element) {
        super(element);
    }

    @Override
    protected VarOrConstReference self() {
        return this;
    }

    @Override
    public VarOrConstSolver newSolver() {
        return new VarOrConstSolver(self());
    }

    @Override
    public void walkSolver(VarOrConstSolver solver) {
        GoPsiScopesUtil.treeWalkUp(
                solver,
                getElement().getParent().getParent(),
                getElement().getContainingFile(),
                ResolveStates.initial());
    }
}
