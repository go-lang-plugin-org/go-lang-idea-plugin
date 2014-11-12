package ro.redeul.google.go.lang.psi.resolve.refs;

import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.processors.ResolveStates;
import ro.redeul.google.go.lang.psi.resolve.ReferenceWithSolver;
import ro.redeul.google.go.lang.psi.typing.GoType;
import ro.redeul.google.go.lang.psi.utils.GoPsiScopesUtil;

public class TypedConstReference
        extends ReferenceWithSolver<GoLiteralIdentifier, TypedConstSolver, TypedConstReference> {

    final private GoType constantType;

    public TypedConstReference(GoLiteralIdentifier element, GoType constantType) {
        super(element);
        this.constantType = constantType;
    }

    @Override
    protected TypedConstSolver newSolver() {
        return new TypedConstSolver(self());
    }

    @Override
    protected TypedConstReference self() {
        return this;
    }

    public GoType getConstantType() {
        return constantType;
    }

    @Override
    protected void walkSolver(TypedConstSolver solver) {
        GoPsiScopesUtil.treeWalkUp(
                solver,
                getElement().getParent().getParent(),
                getElement().getContainingFile(),
                ResolveStates.initial());
    }
}
