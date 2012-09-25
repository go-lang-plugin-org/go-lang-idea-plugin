package ro.redeul.google.go.lang.psi.types;

import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.types.struct.GoTypeStructAnonymousField;

public class GoStructPromotedFields {
    private final GoLiteralIdentifier[] namedFields;
    private final GoTypeStructAnonymousField[] anonymousFields;

    public GoStructPromotedFields(GoLiteralIdentifier[] namedFields, GoTypeStructAnonymousField[] anonymousFields) {
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
