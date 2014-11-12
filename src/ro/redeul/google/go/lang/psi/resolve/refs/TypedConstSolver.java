package ro.redeul.google.go.lang.psi.resolve.refs;

import ro.redeul.google.go.lang.psi.declarations.GoConstDeclaration;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.resolve.ReferenceSolvingVisitor;
import ro.redeul.google.go.lang.psi.resolve.VisitingReferenceSolver;
import ro.redeul.google.go.lang.psi.typing.GoType;
import ro.redeul.google.go.lang.psi.typing.GoTypes;

public class TypedConstSolver extends VisitingReferenceSolver<TypedConstReference, TypedConstSolver> {

    @Override
    public TypedConstSolver self() {
        return this;
    }

    public TypedConstSolver(final TypedConstReference reference) {
        solveWithVisitor(new ReferenceSolvingVisitor(this, reference) {

            @Override
            public void visitConstDeclaration(GoConstDeclaration declaration) {
                String referenceName = reference.name();

                GoType identifiersType = GoTypes.fromPsi(declaration.getIdentifiersType());

                for (GoLiteralIdentifier identifier : declaration.getIdentifiers()) {

                    if (!reference.canSee(identifier, referenceName))
                        continue;

                    GoType identifierType = identifiersType;
                    if (identifierType == GoType.Unknown) {
                        GoExpr expr = declaration.getExpression(identifier);
                        if (expr != null) {
                            GoType[] exprType = expr.getType();
                            if (exprType.length > 0 && exprType[0] != null)
                                identifierType = exprType[0];
                        }
                    }

                    if ( ! reference.getConstantType().isAssignableFrom(identifierType))
                        continue;

                    if (matchNames(identifier.getName(), referenceName))
                        addTarget(identifier);
                }
            }
        });
    }
}
