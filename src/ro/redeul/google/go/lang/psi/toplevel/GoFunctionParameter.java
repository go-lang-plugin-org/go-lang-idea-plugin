package ro.redeul.google.go.lang.psi.toplevel;

import ro.redeul.google.go.lang.psi.GoPsiElement;
import ro.redeul.google.go.lang.psi.expressions.literals.GoIdentifier;
import ro.redeul.google.go.lang.psi.types.GoType;

public interface GoFunctionParameter extends GoPsiElement {

    public static final GoFunctionParameter[] EMPTY_ARRAY =
        new GoFunctionParameter[0];

    boolean isVariadic();

    GoIdentifier[] getIdentifiers();

    GoType getType();
}
