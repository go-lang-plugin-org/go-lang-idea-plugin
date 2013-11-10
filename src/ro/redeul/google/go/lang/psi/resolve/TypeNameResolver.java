package ro.redeul.google.go.lang.psi.resolve;

import com.intellij.psi.PsiElement;
import ro.redeul.google.go.lang.completion.GoCompletionContributor;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.resolve.references.TypeNameReference;
import ro.redeul.google.go.lang.psi.toplevel.GoTypeNameDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoTypeSpec;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: Sep 21, 2010
 * Time: 4:47:46 AM
 */
public class TypeNameResolver extends GoPsiReferenceResolver<TypeNameReference> {

    public TypeNameResolver(TypeNameReference reference) {
        super(reference);
    }

    @Override
    public void visitTypeSpec(GoTypeSpec type) {
        if (checkReference(type.getTypeNameDeclaration())){
            addDeclaration(type.getTypeNameDeclaration());
        }
    }

    @Override
    boolean checkReference(PsiElement element) {
        if (!(element instanceof GoTypeNameDeclaration)) {
            return false;
        }

        GoTypeNameDeclaration typeNameDeclaration = (GoTypeNameDeclaration) element;
        GoLiteralIdentifier identifier = getReference().getElement().getIdentifier();
        String identifierName = identifier.getUnqualifiedName();
        String typeName = typeNameDeclaration.getName();

        if (identifierName.equals(typeName)) {
            return !identifier.isQualified() ||
                    canonicalNamesAreTheSame(identifier, typeNameDeclaration);
        }

        return isTypeCompletionCandidate(identifierName, typeName);
    }

    // Returns true if identifier is an identifier to be completed and type is a valid completion candidate.
    private boolean isTypeCompletionCandidate(String identifier, String type) {
        if (identifier.endsWith(GoCompletionContributor.DUMMY_IDENTIFIER)) {
            int dummyLength = GoCompletionContributor.DUMMY_IDENTIFIER.length();
            int identifierLength = identifier.length() - dummyLength;
            return type.startsWith(identifier.substring(0, identifierLength));
        }
        return false;
    }

    private boolean canonicalNamesAreTheSame(GoLiteralIdentifier identifier,
                                             GoTypeNameDeclaration typeNameDeclaration) {
        String identifierName = identifier.getCanonicalName().toLowerCase();
        String typeName = typeNameDeclaration.getCanonicalName().toLowerCase();
        return identifierName.equals(typeName);
    }
}
