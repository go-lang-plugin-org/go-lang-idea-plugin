package ro.redeul.google.go.lang.psi.toplevel;

import ro.redeul.google.go.lang.psi.GoPsiElement;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.types.GoPsiType;

public interface GoFunctionParameter extends GoPsiElement {

    public static final GoFunctionParameter[] EMPTY_ARRAY =
        new GoFunctionParameter[0];

    boolean isVariadic();

    GoLiteralIdentifier[] getIdentifiers();

    GoPsiType getType();
}
