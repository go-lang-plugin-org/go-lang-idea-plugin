package ro.redeul.google.go.lang.psi.resolve.refs;

import ro.redeul.google.go.lang.psi.resolve.ReferenceSolvingVisitor;
import ro.redeul.google.go.lang.psi.resolve.VisitingReferenceSolver;
import ro.redeul.google.go.lang.psi.toplevel.GoMethodDeclaration;
import ro.redeul.google.go.lang.psi.typing.GoType;
import ro.redeul.google.go.lang.psi.typing.GoTypeName;
import ro.redeul.google.go.lang.psi.typing.GoTypePointer;
import ro.redeul.google.go.lang.psi.typing.GoTypes;

import java.util.Set;

public class MethodSolver extends VisitingReferenceSolver<MethodReference, MethodSolver>  {

    @Override
    public MethodSolver self() { return this; }

    public MethodSolver(final MethodReference reference) {
        final Set<GoTypeName> embeddedTypes = reference.findEmbeddedTypes(reference.getTypeName());

        solveWithVisitor(new ReferenceSolvingVisitor(this, reference) {
            @Override
            public void visitMethodDeclaration(GoMethodDeclaration declaration) {
                if (isReferenceTo(declaration))
                    addTarget(declaration);
            }

            boolean isReferenceTo(GoMethodDeclaration declaration) {
                GoType receiverType = GoTypes.fromPsi(declaration.getMethodReceiver().getType());

                if ( receiverType instanceof GoTypePointer )
                    receiverType = ((GoTypePointer) receiverType).getTargetType();

                if (!(receiverType instanceof GoTypeName))
                    return false;

                GoTypeName methodTypeName = (GoTypeName) receiverType;

                for (GoTypeName type : embeddedTypes) {

                    if (type.getName().equals(methodTypeName.getName())) {
                        String methodName = declaration.getFunctionName();

                        return matchNames(reference.name(), methodName);
                    }
                }

                return false;
            }
        });
    }
}
