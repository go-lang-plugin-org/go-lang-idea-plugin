package ro.redeul.google.go.lang.psi.resolve;

import ro.redeul.google.go.lang.psi.declarations.GoConstDeclaration;
import ro.redeul.google.go.lang.psi.declarations.GoVarDeclaration;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.resolve.references.VarOrConstReference;
import ro.redeul.google.go.lang.psi.statements.GoShortVarDeclaration;
import ro.redeul.google.go.lang.psi.statements.switches.GoSwitchTypeGuard;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionParameter;
import ro.redeul.google.go.lang.psi.toplevel.GoMethodReceiver;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: 5/22/11
 * Time: 8:35 PM
 */
public class VarOrConstSolver extends RefSolver<VarOrConstReference, VarOrConstSolver> {

    public VarOrConstSolver(VarOrConstReference reference) {
        super(reference);
    }

    @Override
    public void visitMethodReceiver(GoMethodReceiver receiver) {
        checkIdentifiers(getReferenceName(), receiver.getIdentifier());
    }

    @Override
    public void visitFunctionParameter(GoFunctionParameter parameter) {
        checkIdentifiers(getReferenceName(), parameter.getIdentifiers());
    }

    @Override
    public void visitVarDeclaration(GoVarDeclaration declaration) {
        checkIdentifiers(getReferenceName(), declaration.getIdentifiers());
    }

    @Override
    public void visitShortVarDeclaration(GoShortVarDeclaration declaration) {
        checkIdentifiers(getReferenceName(), declaration.getDeclarations());
    }

    @Override
    public void visitLiteralIdentifier(GoLiteralIdentifier identifier) {
        checkIdentifiers(getReferenceName(), identifier);
    }

    @Override
    public void visitConstDeclaration(GoConstDeclaration declaration) {
        checkIdentifiers(getReferenceName(), declaration.getIdentifiers());
    }

    @Override
    public void visitFunctionDeclaration(GoFunctionDeclaration declaration) {
        if ( matchNames(getReferenceName(), declaration.getFunctionName()))
            addTarget(declaration);
    }

    @Override
    public void visitSwitchTypeGuard(GoSwitchTypeGuard typeGuard) {
        checkIdentifiers(getReferenceName(), typeGuard.getIdentifier());
    }
}
