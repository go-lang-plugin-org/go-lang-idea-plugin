package ro.redeul.google.go.lang.psi.resolve.refs;

import ro.redeul.google.go.lang.psi.declarations.GoConstDeclaration;
import ro.redeul.google.go.lang.psi.declarations.GoVarDeclaration;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.resolve.ReferenceSolvingVisitor;
import ro.redeul.google.go.lang.psi.resolve.VisitingReferenceSolver;
import ro.redeul.google.go.lang.psi.statements.GoShortVarDeclaration;
import ro.redeul.google.go.lang.psi.statements.switches.GoSwitchTypeGuard;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionParameter;
import ro.redeul.google.go.lang.psi.toplevel.GoMethodDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoMethodReceiver;

public class VarOrConstSolver extends VisitingReferenceSolver<VarOrConstReference, VarOrConstSolver> {

    @Override
    public VarOrConstSolver self() { return this; }

    public VarOrConstSolver(final VarOrConstReference reference) {
        solveWithVisitor(new ReferenceSolvingVisitor(this, reference) {
            @Override
            public void visitMethodReceiver(GoMethodReceiver receiver) {
                if ( receiver.getIdentifier() != null )
                    checkIdentifiers(reference.name(), receiver.getIdentifier());
            }

            @Override
            public void visitFunctionParameter(GoFunctionParameter parameter) {
                checkIdentifiers(reference.name(), parameter.getIdentifiers());
            }

            @Override
            public void visitVarDeclaration(GoVarDeclaration declaration) {
                checkIdentifiers(reference.name(), declaration.getIdentifiers());
            }

            @Override
            public void visitShortVarDeclaration(GoShortVarDeclaration declaration) {
                checkIdentifiers(reference.name(), declaration.getIdentifiers());
            }

            @Override
            public void visitLiteralIdentifier(GoLiteralIdentifier identifier) {
                checkIdentifiers(reference.name(), identifier);
            }

            @Override
            public void visitConstDeclaration(GoConstDeclaration declaration) {
                checkIdentifiers(reference.name(), declaration.getIdentifiers());
            }

            @Override
            public void visitFunctionDeclaration(GoFunctionDeclaration declaration) {
                if (matchNames(reference.name(), declaration.getFunctionName()))
                    addTarget(declaration);
            }

            @Override
            public void visitMethodDeclaration(GoMethodDeclaration declaration) {
//                visitFunctionDeclaration(declaration);
            }

            @Override
            public void visitSwitchTypeGuard(GoSwitchTypeGuard typeGuard) {
                checkIdentifiers(reference.name(), typeGuard.getIdentifier());
            }
        });
    }
}
