package ro.redeul.google.go.lang.psi.resolve.refs;

import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.processors.ResolveStates;
import ro.redeul.google.go.lang.psi.resolve.ReferenceWithSolver;
import ro.redeul.google.go.lang.psi.utils.GoPsiScopesUtil;

public class FunctionOrTypeNameReference extends ReferenceWithSolver<GoLiteralIdentifier, FunctionOrTypeNameSolver, FunctionOrTypeNameReference> {

    public FunctionOrTypeNameReference(GoLiteralIdentifier element) {
        super(element);
    }

    @Override
    protected FunctionOrTypeNameReference self() { return this; }

    @Override
    public FunctionOrTypeNameSolver newSolver() { return new FunctionOrTypeNameSolver(this); }

    @Override
    public void walkSolver(FunctionOrTypeNameSolver solver) {
        GoPsiScopesUtil.treeWalkUp(
                solver,
                getElement(),
                getElement().getContainingFile(),
                ResolveStates.initial());
    }
}
