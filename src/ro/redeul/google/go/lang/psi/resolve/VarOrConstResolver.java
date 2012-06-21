package ro.redeul.google.go.lang.psi.resolve;

import ro.redeul.google.go.lang.psi.declarations.GoConstDeclaration;
import ro.redeul.google.go.lang.psi.declarations.GoVarDeclaration;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.resolve.references.VarOrConstReference;
import ro.redeul.google.go.lang.psi.statements.GoShortVarDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionParameter;
import ro.redeul.google.go.lang.psi.toplevel.GoMethodReceiver;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: 5/22/11
 * Time: 8:35 PM
 */
public class VarOrConstResolver extends
                                GoPsiReferenceResolver<VarOrConstReference> {

    public VarOrConstResolver(VarOrConstReference reference) {
        super(reference);
    }

    @Override
    public void visitMethodReceiver(GoMethodReceiver receiver) {
        checkIdentifiers(receiver.getIdentifier());
    }

    @Override
    public void visitFunctionParameter(GoFunctionParameter parameter) {
        checkIdentifiers(parameter.getIdentifiers());
    }

    @Override
    public void visitVarDeclaration(GoVarDeclaration declaration) {
        checkIdentifiers(declaration.getIdentifiers());
    }

    @Override
    public void visitShortVarDeclaration(GoShortVarDeclaration declaration) {
        checkIdentifiers(declaration.getIdentifiers());
    }

    @Override
    public void visitLiteralIdentifier(GoLiteralIdentifier identifier) {
        checkIdentifiers(identifier);
    }

    @Override
    public void visitConstDeclaration(GoConstDeclaration declaration) {
        checkIdentifiers(declaration.getIdentifiers());
    }

    protected void checkIdentifiers(GoLiteralIdentifier ... identifiers) {
        for (GoLiteralIdentifier id : identifiers) {
            if (checkReference(id)) {
                if ( ! addDeclaration(id) )
                    return;
            }
        }
    }
}