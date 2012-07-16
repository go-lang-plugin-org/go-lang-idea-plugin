package ro.redeul.google.go.lang.psi.resolve;

import ro.redeul.google.go.lang.psi.resolve.references.MethodReference;
import ro.redeul.google.go.lang.psi.toplevel.GoMethodDeclaration;

public class MethodResolver extends GoPsiReferenceResolver<MethodReference>
{
    public MethodResolver(MethodReference reference) {
        super(reference);
    }

    @Override
    public void visitMethodDeclaration(GoMethodDeclaration declaration) {
        if ( getReference().isReferenceTo(declaration))
            addDeclaration(declaration, declaration.getNameIdentifier());
    }
}
