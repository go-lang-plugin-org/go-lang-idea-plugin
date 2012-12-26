package ro.redeul.google.go.lang.psi.types.struct;

import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;

public class GoTypeStructPromotedFields {
    private final GoLiteralIdentifier[] namedFields;
    private final GoTypeStructAnonymousField[] anonymousFields;

    public GoTypeStructPromotedFields(GoLiteralIdentifier[] namedFields, GoTypeStructAnonymousField[] anonymousFields) {
        this.namedFields = namedFields;
        this.anonymousFields = anonymousFields;
    }

    public GoLiteralIdentifier[] getNamedFields() {
        return namedFields;
    }

    public GoTypeStructAnonymousField[] getAnonymousFields() {
        return anonymousFields;
    }
}
