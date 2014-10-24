package ro.redeul.google.go.lang.psi.resolve.refs;

import ro.redeul.google.go.lang.psi.declarations.GoConstDeclaration;
import ro.redeul.google.go.lang.psi.declarations.GoVarDeclaration;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.resolve.ReferenceSolvingVisitor;
import ro.redeul.google.go.lang.psi.resolve.VisitingReferenceSolver;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoMethodDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoTypeDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoTypeSpec;

public class PackageSymbolSolver extends VisitingReferenceSolver<PackageSymbolReference, PackageSymbolSolver> {

    @Override
    public PackageSymbolSolver self() { return this; }

    public PackageSymbolSolver(final PackageSymbolReference reference) {
        solveWithVisitor(new ReferenceSolvingVisitor(this, reference) {
            @Override
            public void visitConstDeclaration(GoConstDeclaration declaration) {
                checkIdentifiers(reference.name(), declaration.getIdentifiers());
            }

            @Override
            public void visitVarDeclaration(GoVarDeclaration declaration) {
                checkIdentifiers(reference.name(), declaration.getIdentifiers());
            }

            @Override
            public void visitMethodDeclaration(GoMethodDeclaration declaration) {
                visitFunctionDeclaration(declaration);
            }

            @Override
            public void visitFunctionDeclaration(GoFunctionDeclaration declaration) {
                if (matchNames(reference.name(), declaration.getFunctionName()))
                    addTarget(declaration);
            }

            @Override
            public void visitTypeSpec(GoTypeSpec type) {
                String name = type.getName();
                if (name != null && matchNames(reference.name(), name))
                    addTarget(type);
            }

            boolean isReferenceTo(GoConstDeclaration constDeclaration) {
                for (GoLiteralIdentifier identifier : constDeclaration.getIdentifiers())
                    if (matchNames(reference.name(), identifier.getUnqualifiedName()))
                        return true;

                return false;
            }

            boolean isReferenceTo(GoVarDeclaration varDeclaration) {
                for (GoLiteralIdentifier identifier : varDeclaration.getIdentifiers())
                    if (matchNames(reference.name(), identifier.getUnqualifiedName()))
                        return true;

                return false;
            }
        });
    }
}
