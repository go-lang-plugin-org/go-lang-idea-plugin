package ro.redeul.google.go.lang.psi.resolve;

import ro.redeul.google.go.lang.psi.declarations.GoVarDeclaration;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.resolve.references.CallOrConversionReference;
import ro.redeul.google.go.lang.psi.statements.GoShortVarDeclaration;
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
            addDeclaration(declaration, declaration.getNameIdentifier());
    }

    @Override
    public void visitMethodDeclaration(GoMethodDeclaration declaration) {
        if  ( checkReference(declaration) )
            addDeclaration(declaration, declaration.getNameIdentifier());
    }

    @Override
    public void visitTypeSpec(GoTypeSpec type) {
        if ( checkReference(type.getTypeNameDeclaration()) )
            addDeclaration(type);
    }

    @Override
    public void visitVarDeclaration(GoVarDeclaration declaration) {
        if (checkReference(declaration))
            addDeclaration(declaration);
    }

    @Override
    public void visitShortVarDeclaration(GoShortVarDeclaration declaration) {

        GoLiteralIdentifier identifiers[] = declaration.getIdentifiers();
        GoExpr expressions[] = declaration.getExpressions();

        for (int i = 0; i < identifiers.length; i++) {
            GoLiteralIdentifier identifier = identifiers[i];
            if (expressions.length > i) {
                GoExpr expr = expressions[i];
                if (expr.getType().length == 1 && (expr.getType()[0] instanceof GoTypeFunction)) {
                    if ( checkReference(identifier) ) {
                        if ( ! addDeclaration(identifier) ) {
                            return;
                        }
                    }
                }
            }
        }
    }

    private boolean checkVarDeclaration(GoLiteralIdentifier identifier,
                                        GoShortVarDeclaration declaration) {
        declaration.getIdentifiersType();
        return false;  //To change body of created methods use File | Settings | File Templates.
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
