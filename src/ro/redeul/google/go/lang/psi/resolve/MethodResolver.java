package ro.redeul.google.go.lang.psi.resolve;

import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.resolve.references.MethodReference;
import ro.redeul.google.go.lang.psi.toplevel.GoMethodDeclaration;
import ro.redeul.google.go.lang.psi.types.GoPsiType;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeName;
import ro.redeul.google.go.lang.psi.types.GoPsiTypePointer;
import ro.redeul.google.go.lang.psi.typing.GoTypeName;

import java.util.Set;

import static ro.redeul.google.go.lang.completion.GoCompletionContributor.DUMMY_IDENTIFIER;

public class MethodResolver extends GoPsiReferenceResolver<MethodReference>
{
    public MethodResolver(MethodReference reference) {
        super(reference);
    }

    @Override
    public void visitMethodDeclaration(GoMethodDeclaration declaration) {
        if (isReferenceTo(declaration))
            addDeclaration(declaration, declaration.getNameIdentifier());
    }

    public boolean isReferenceTo(GoMethodDeclaration declaration) {
        GoPsiType receiverType = declaration.getMethodReceiver().getType();

        if (receiverType == null)
            return false;

        if (receiverType instanceof GoPsiTypePointer) {
            receiverType = ((GoPsiTypePointer) receiverType).getTargetType();
        }

        if (!(receiverType instanceof GoPsiTypeName))
            return false;

        GoPsiTypeName methodTypeName = (GoPsiTypeName) receiverType;

        Set<GoTypeName> receiverTypes = getReference().resolveBaseReceiverTypes();

        GoLiteralIdentifier identifier = getReference().getElement().getIdentifier();
        if (identifier == null) {
            return false;
        }
        for (GoTypeName type : receiverTypes) {
            if ( type.getName().equals(methodTypeName.getName())) {
                String methodName = declaration.getFunctionName();
                String referenceName = identifier.getUnqualifiedName();

                return referenceName.contains(DUMMY_IDENTIFIER) ||
                        referenceName.equals(methodName);
            }
        }

        return false;
    }
}
