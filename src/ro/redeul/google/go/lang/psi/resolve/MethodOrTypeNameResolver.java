package ro.redeul.google.go.lang.psi.resolve;

import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.resolve.references.CallOrConversionReference;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionParameter;
import ro.redeul.google.go.lang.psi.toplevel.GoMethodDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoTypeSpec;
import ro.redeul.google.go.lang.psi.types.GoTypeFunction;

public class MethodOrTypeNameResolver extends GoPsiReferenceResolver<CallOrConversionReference>
{
    public MethodOrTypeNameResolver(CallOrConversionReference reference) {
        super(reference);
    }

    @Override
    public void visitFunctionDeclaration(GoFunctionDeclaration declaration) {
        if  ( checkReference(declaration) )
            addDeclaration(declaration);
    }

    @Override
    public void visitMethodDeclaration(GoMethodDeclaration declaration) {
        if  ( checkReference(declaration) )
            addDeclaration(declaration);
    }

    @Override
    public void visitTypeSpec(GoTypeSpec type) {
        if ( checkReference(type.getTypeNameDeclaration()) )
            addDeclaration(type);
    }

    @Override
    public void visitFunctionParameter(GoFunctionParameter parameter) {
        if ( parameter.getType() instanceof GoTypeFunction) {
            for (GoLiteralIdentifier identifier : parameter.getIdentifiers()) {
                if (checkReference(identifier)) {
                    if ( !addDeclaration(identifier) ) {
                        return;
                    }
                }
            }
        }
    }
}
