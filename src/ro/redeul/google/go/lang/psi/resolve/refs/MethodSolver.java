package ro.redeul.google.go.lang.psi.resolve.refs;

import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.resolve.ReferenceSolvingVisitor;
import ro.redeul.google.go.lang.psi.resolve.VisitingReferenceSolver;
import ro.redeul.google.go.lang.psi.toplevel.GoMethodDeclaration;
import ro.redeul.google.go.lang.psi.types.GoPsiType;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeName;
import ro.redeul.google.go.lang.psi.types.GoPsiTypePointer;
import ro.redeul.google.go.lang.psi.typing.GoTypeName;

import java.util.Set;

public class MethodSolver extends VisitingReferenceSolver<MethodReference, MethodSolver>  {

    @Override
    public MethodSolver self() { return this; }

    public MethodSolver(final MethodReference reference) {
        solveWithVisitor(new ReferenceSolvingVisitor(this, reference) {
            @Override
            public void visitMethodDeclaration(GoMethodDeclaration declaration) {
                if (isReferenceTo(declaration))
                    addTarget(declaration);
            }

            boolean isReferenceTo(GoMethodDeclaration declaration) {
                GoPsiType receiverType = declaration.getMethodReceiver().getType();

                if (receiverType == null)
                    return false;

                if (receiverType instanceof GoPsiTypePointer) {
                    receiverType = ((GoPsiTypePointer) receiverType).getTargetType();
                }

                if (!(receiverType instanceof GoPsiTypeName))
                    return false;

                GoPsiTypeName methodTypeName = (GoPsiTypeName) receiverType;

                Set<GoTypeName> receiverTypes = reference.resolveBaseReceiverTypes();

                GoLiteralIdentifier identifier = reference.getElement();
                if (identifier == null) {
                    return false;
                }

                for (GoTypeName type : receiverTypes) {
                    if ( type.getName().equals(methodTypeName.getName())) {
                        String methodName = declaration.getFunctionName();

                        return matchNames(referenceName(), methodName);
                    }
                }

                return false;
            }
        });
    }

}
