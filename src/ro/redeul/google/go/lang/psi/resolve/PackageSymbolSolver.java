package ro.redeul.google.go.lang.psi.resolve;

import ro.redeul.google.go.lang.psi.declarations.GoConstDeclaration;
import ro.redeul.google.go.lang.psi.declarations.GoVarDeclaration;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.resolve.references.PackageSymbolReference;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoMethodDeclaration;

public class PackageSymbolSolver extends RefSolver<PackageSymbolReference, PackageSymbolSolver> {
    public PackageSymbolSolver(PackageSymbolReference reference) {
        super(reference);
    }

    @Override
    public void visitConstDeclaration(GoConstDeclaration declaration) {
        checkIdentifiers(getReferenceName(), declaration.getIdentifiers());
    }

    @Override
    public void visitVarDeclaration(GoVarDeclaration declaration) {
        checkIdentifiers(getReference().getReferenceElement().getUnqualifiedName(), declaration.getIdentifiers());
    }

    @Override
    public void visitMethodDeclaration(GoMethodDeclaration declaration) {
        visitFunctionDeclaration(declaration);
    }

    @Override
    public void visitFunctionDeclaration(GoFunctionDeclaration declaration) {
        if (matchNames(declaration.getFunctionName(), getReferenceName()))
            addTarget(declaration);
    }

    boolean isReferenceTo(GoConstDeclaration constDeclaration) {
        for (GoLiteralIdentifier identifier : constDeclaration.getIdentifiers())
            if (matchNames(identifier.getUnqualifiedName(), getReferenceName()))
                return true;

        return false;
    }

    boolean isReferenceTo(GoVarDeclaration varDeclaration) {
        for (GoLiteralIdentifier identifier : varDeclaration.getIdentifiers())
            if (matchNames(identifier.getUnqualifiedName(), getReferenceName()))
                return true;

        return false;
    }
}
