package ro.redeul.google.go.lang.psi.resolve.refs;

import ro.redeul.google.go.lang.psi.declarations.GoConstDeclaration;
import ro.redeul.google.go.lang.psi.declarations.GoVarDeclaration;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.resolve.ReferenceSolvingVisitor;
import ro.redeul.google.go.lang.psi.resolve.VisitingReferenceSolver;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoMethodDeclaration;

public class PackageSymbolSolver extends VisitingReferenceSolver<PackageSymbolReference, PackageSymbolSolver> {

    @Override
    public PackageSymbolSolver self() { return this; }

    public PackageSymbolSolver(PackageSymbolReference reference) {
        solveWithVisitor(new ReferenceSolvingVisitor(this, reference) {
            @Override
            public void visitConstDeclaration(GoConstDeclaration declaration) {
                checkIdentifiers(referenceName(), declaration.getIdentifiers());
            }

            @Override
            public void visitVarDeclaration(GoVarDeclaration declaration) {
                checkIdentifiers(referenceName(), declaration.getIdentifiers());
            }

            @Override
            public void visitMethodDeclaration(GoMethodDeclaration declaration) {
                visitFunctionDeclaration(declaration);
            }

            @Override
            public void visitFunctionDeclaration(GoFunctionDeclaration declaration) {
                if (matchNames(referenceName(), declaration.getFunctionName()))
                    addTarget(declaration);
            }

            boolean isReferenceTo(GoConstDeclaration constDeclaration) {
                for (GoLiteralIdentifier identifier : constDeclaration.getIdentifiers())
                    if (matchNames(referenceName(), identifier.getUnqualifiedName()))
                        return true;

                return false;
            }

            boolean isReferenceTo(GoVarDeclaration varDeclaration) {
                for (GoLiteralIdentifier identifier : varDeclaration.getIdentifiers())
                    if (matchNames(referenceName(), identifier.getUnqualifiedName()))
                        return true;

                return false;
            }
        });
    }
}
