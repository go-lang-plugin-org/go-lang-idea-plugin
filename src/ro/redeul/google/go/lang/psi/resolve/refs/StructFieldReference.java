package ro.redeul.google.go.lang.psi.resolve.refs;

import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.processors.ResolveStates;
import ro.redeul.google.go.lang.psi.resolve.ReferenceWithSolver;
import ro.redeul.google.go.lang.psi.typing.GoTypeStruct;

public class StructFieldReference
    extends ReferenceWithSolver<GoLiteralIdentifier, StructFieldSolver, StructFieldReference> {

    private GoTypeStruct typeStruct;

    public StructFieldReference(GoLiteralIdentifier identifier, GoTypeStruct typeStruct) {
        super(identifier);
        this.typeStruct = typeStruct;
    }
    @Override
    protected StructFieldReference self() {
        return this;
    }

    @Override
    protected StructFieldSolver newSolver() {
        return new StructFieldSolver(self());
    }

    @Override
    protected void walkSolver(StructFieldSolver solver) {
        typeStruct.getPsiType().processDeclarations(solver, ResolveStates.initial(), null, getElement());
    }
}
