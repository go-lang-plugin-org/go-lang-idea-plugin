package ro.redeul.google.go.lang.psi.resolve.refs;

import ro.redeul.google.go.lang.psi.resolve.ReferenceSolvingVisitor;
import ro.redeul.google.go.lang.psi.resolve.VisitingReferenceSolver;
import ro.redeul.google.go.lang.psi.types.struct.GoTypeStructAnonymousField;
import ro.redeul.google.go.lang.psi.types.struct.GoTypeStructField;

public class CompositeLiteralKeySolver extends VisitingReferenceSolver<CompositeLiteralKeyReference, CompositeLiteralKeySolver> {

    @Override
    public CompositeLiteralKeySolver self() { return this; }

    public CompositeLiteralKeySolver(CompositeLiteralKeyReference reference) {
        solveWithVisitor(new ReferenceSolvingVisitor(this, reference) {
            @Override
            public void visitTypeStructField(GoTypeStructField field) {
                checkIdentifiers(referenceName(), field.getIdentifiers());
            }

            @Override
            public void visitTypeStructAnonymousField(GoTypeStructAnonymousField field) {
                if (matchNames(referenceName(), field.getFieldName()))
                    addTarget(field);
            }
        });
    }
}
